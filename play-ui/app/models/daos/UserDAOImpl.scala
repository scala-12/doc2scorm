package models.daos

import java.util.UUID
import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import models.daos.UserDAOImpl._
import scala.collection.mutable
import scala.concurrent.Future
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

/**
 * Give access to the user object.
 */
class UserDAOImpl @Inject() (dbConfigProvider: DatabaseConfigProvider) extends UserDAO {

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo) = Future.successful(
    cacheUsers.find { case (id, user) => user.loginInfo == loginInfo }.map(_._2)
  )

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: UUID) = Future.successful(cacheUsers.get(userID))

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User) = {
    cacheUsers += (user.userID -> user)
    
    Future.successful(user)
  }
}

/**
 * The companion object.
 */
object UserDAOImpl {

  /**
   * The list of users.
   */
  val cacheUsers: mutable.HashMap[UUID, User] = mutable.HashMap()
}
