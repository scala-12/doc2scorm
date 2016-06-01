package utils.actors

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Address, Props, RootActorPath}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member}
import akka.pattern
import akka.routing.{ActorRefRoutee, Router, SmallestMailboxRoutingLogic}
import akka.util.Timeout
import com.ipoint.coursegenerator.core.utils.ImageFormatConverter
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object HostConvertSupervisor extends Actor with ActorLogging {
  val rootConf = ConfigFactory load()
  val clusterConf = rootConf.getObject("cluster").toConfig

  val protocol = clusterConf getString "protocol"
  val system = clusterConf getString "system-name"

  var seedList = clusterConf.getStringList("cluster-seed-nodes").toList
  if ((seedList.length == 1) && seedList.head.contains(",")) {
    //parse from console
    seedList = seedList.head.split(',').toList
  }

  val seedNodes = seedList.map { address =>
    val addressSplit = address split ':'
    Address(protocol, system, addressSplit(0), addressSplit(1).toInt)
  }

  log.info(s"Seed nodes: $seedNodes")

  val hostAddress: String = s"${clusterConf getString "akka.remote.netty.tcp.hostname"}:" +
    (if (clusterConf.getInt("akka.remote.netty.tcp.port") == 0)
      UUID.randomUUID().toString
    else
      clusterConf getString "akka.remote.netty.tcp.port")

  val supervisorName = self.path.name

  val cluster = Cluster(context.system)
  cluster joinSeedNodes seedNodes

  if (!rootConf.getIsNull("libreoffice.program.path")) {
    ImageFormatConverter.setPathToOffice(rootConf getString "libreoffice.program.path")
  }

  override def preStart(): Unit = {
    cluster subscribe(self, InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  val hosts: mutable.HashMap[Member, ActorRef] = mutable.HashMap.empty

  var hostRouter = {
    context watch self
    Router(
      SmallestMailboxRoutingLogic(),
      Vector.fill(if (clusterConf.getInt("converter-count") == 0) 0 else 1) {
        ActorRefRoutee(self)
      })
  }

  val workerRouter = if (clusterConf.getInt("converter-count") == 0) {
    null
  } else {
    val routees = Vector.fill(clusterConf.getInt("converter-count")) {
      val converter = context actorOf(Props(classOf[ConvertActor]), NameUtils.createName)
      context watch converter
      ActorRefRoutee(converter)
    }

    Router(SmallestMailboxRoutingLogic(), routees)
  }

  def receive = {
    case ConversionOnHost(courseDocBytes, maxHeader, courseName) =>
      if (clusterConf.getInt("converter-count") > 0) {
        workerRouter.route(ConvertActor.Conversion(courseDocBytes, maxHeader, courseName), sender())
      }

    case ConversionInCluster(courseDocBytes, maxHeader, courseName) =>
      self ! DelayConversionInCluster(sender(), courseDocBytes, maxHeader, courseName)

    case DelayConversionInCluster(waiter, courseDocBytes, maxHeader, courseName) =>
      if (hostRouter.routees.isEmpty) {
        context.system.scheduler.scheduleOnce(1.seconds, self, DelayConversionInCluster(waiter, courseDocBytes, maxHeader, courseName))
      } else {
        hostRouter.route(ConversionOnHost(courseDocBytes, maxHeader, courseName), waiter)
      }

    case ThisHost => sender() ! (if (clusterConf.getInt("converter-count") == 0) None else Some(self))

    case MemberUp(member) =>
      log.info(s"[Listener] node is up: $member")

      if (hostAddress != s"${
        member.address.host.get
      }:${
        member.address.port.get
      }") {
        implicit val timeout = Timeout(15.seconds)

        val newHostFuture = pattern.ask(
          context.actorSelection(RootActorPath(member.address) / "user" / supervisorName),
          ThisHost).asInstanceOf[Future[Option[ActorRef]]]

        newHostFuture.flatMap(newHost =>
          Future successful {
            if (newHost.isDefined) {
              hosts.put(member, newHost.get)
              hostRouter = hostRouter.addRoutee(newHost.get)
            }
          }
        )
      }

    case UnreachableMember(member) => log info s"[Listener] node is unreachable: $member"

    case MemberRemoved(member, prevStatus) =>
      log info s"[Listener] node is removed: $member after $prevStatus"
      if ((hostAddress != s"${
        member.address.host.get
      }:${
        member.address.port.get
      }")
        && hosts.contains(member)) {
        hostRouter = hostRouter.removeRoutee(hosts.remove(member).get)
      }
  }


  case class ConversionOnHost(courseDocBytes: Array[Byte], maxHeader: Int, courseName: String)

  case class ConversionInCluster(courseDocBytes: Array[Byte], maxHeader: Int, courseName: String)

  private case class DelayConversionInCluster(waiter: ActorRef, courseDocBytes: Array[Byte], maxHeader: Int, courseName: String)

  case object ThisHost

}