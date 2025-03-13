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
  private val rootConf = ConfigFactory load()
  private val akkaClusterConf = rootConf.getObject("ipoint-conf.akka-cluster").toConfig

  private val protocol = akkaClusterConf getString "const.protocol"
  private val system = akkaClusterConf getString "const.system-name"

  private var seedList = akkaClusterConf.getStringList("cluster-seed-nodes").toList
  if ((seedList.length == 1) && seedList.head.contains(",")) {
    //parse from console
    seedList = seedList.head.split(',').toList
  }

  private val seedNodes = seedList.map { address =>
    val addressSplit = address split ':'
    Address(protocol, system, addressSplit(0), addressSplit(1).toInt)
  }

  log.info(s"Seed nodes: $seedNodes")

  val hostAddress: String = s"${akkaClusterConf getString "host"}:" +
    (if (akkaClusterConf.getInt("port") == 0)
      UUID.randomUUID().toString
    else
      akkaClusterConf getString "port")

  private val supervisorName = self.path.name

  private val cluster = Cluster(context.system)
  cluster joinSeedNodes seedNodes

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
      Vector.fill(if (akkaClusterConf.getInt("converter-count") == 0) 0 else 1) {
        ActorRefRoutee(self)
      })
  }

  private val workerRouter = if (akkaClusterConf.getInt("converter-count") == 0) {
    null
  } else {
    val routees = Vector.fill(akkaClusterConf.getInt("converter-count")) {
      val converter = context actorOf(Props(classOf[ConvertActor]), NameUtils.createName)
      context watch converter
      ActorRefRoutee(converter)
    }

    Router(SmallestMailboxRoutingLogic(), routees)
  }

  def receive = {
    case ConversionOnHost(courseDocBytes, maxHeader, courseName) =>
      if (akkaClusterConf.getInt("converter-count") > 0) {
        workerRouter.route(ConvertActor.Conversion(courseDocBytes, maxHeader, courseName), sender())
      }

    case PreviewOnHost(courseDocBytes, maxHeader) =>
      if (akkaClusterConf.getInt("converter-count") > 0) {
        workerRouter.route(ConvertActor.Preview(courseDocBytes, maxHeader), sender())
      }

    case ConversionInCluster(courseDocBytes, maxHeader, courseName) =>
      self ! DelayConversionInCluster(sender(), courseDocBytes, maxHeader, courseName)

    case PreviewInCluster(courseDocBytes, maxHeader) =>
      self ! DelayPreviewInCluster(sender(), courseDocBytes, maxHeader)

    case DelayConversionInCluster(waiter, courseDocBytes, maxHeader, courseName) =>
      if (hostRouter.routees.isEmpty) {
        context.system.scheduler.scheduleOnce(1.seconds, self, DelayConversionInCluster(waiter, courseDocBytes, maxHeader, courseName))
      } else {
        hostRouter.route(ConversionOnHost(courseDocBytes, maxHeader, courseName), waiter)
      }

    case DelayPreviewInCluster(waiter, courseDocBytes, maxHeader) =>
      if (hostRouter.routees.isEmpty) {
        context.system.scheduler.scheduleOnce(1.seconds, self, DelayPreviewInCluster(waiter, courseDocBytes, maxHeader))
      } else {
        hostRouter.route(PreviewOnHost(courseDocBytes, maxHeader), waiter)
      }

    case ThisHost => sender() ! (if (akkaClusterConf.getInt("converter-count") == 0) None else Some(self))

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
  case class PreviewOnHost(courseDocBytes: Array[Byte], maxHeader: Int)

  case class ConversionInCluster(courseDocBytes: Array[Byte], maxHeader: Int, courseName: String)
  case class PreviewInCluster(courseDocBytes: Array[Byte], maxHeader: Int)

  private case class DelayConversionInCluster(waiter: ActorRef, courseDocBytes: Array[Byte], maxHeader: Int, courseName: String)
  private case class DelayPreviewInCluster(waiter: ActorRef, courseDocBytes: Array[Byte], maxHeader: Int)

  case object ThisHost

}