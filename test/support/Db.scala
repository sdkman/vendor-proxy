package support

import slick.jdbc.PostgresProfile.api._
import slick.jdbc.{GetResult, SetParameter}
import utils.TokenGenerator.generateConsumerKey

import java.sql.Types
import java.util.UUID
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object Db extends DatabaseConnection {

  case class Vendor(id: String, name: String, token: String)

  def truncate(): Int = exec(
    for {
      res1 <- sqlu"TRUNCATE TABLE candidates CASCADE "
      res2 <- sqlu"TRUNCATE TABLE credentials CASCADE "
    } yield res1 + res2
  )

  def consumerExists(consumer: String): Boolean = exec(consumerExistsAction(consumer)).isDefined

  def consumerKey(candidate: String): Option[String] = exec(consumerKeyAction(candidate))

  def consumerToken(candidate: String): Option[String] = exec(consumerTokenAction(candidate))

  def saveConsumer(owner: String, token: String, candidates: Seq[String] = Seq.empty): Unit =
    exec(saveConsumerAndCandidatesAction(owner, token, candidates))

  def consumerCandidates(owner: String): Seq[String] = exec(consumerCandidatesAction(owner))

  def exec[T](action: DBIO[T]): T = Await.result(database.run(action), 2 seconds)

  private implicit def helpersSlickGetResultUUID: GetResult[UUID] =
    GetResult(r => r.nextObject.asInstanceOf[UUID])

  private implicit def slickSetParameterUUID: SetParameter[UUID] =
    SetParameter { case (v, pp) => pp.setObject(v, Types.OTHER) }

  private def consumerExistsAction(owner: String) =
    sql"SELECT id FROM credentials WHERE owner = $owner"
      .as[String]
      .headOption

  private def consumerKeyAction(owner: String) =
    sql"SELECT key FROM credentials WHERE owner = $owner"
      .as[String]
      .headOption

  private def consumerTokenAction(owner: String) =
    sql"SELECT token FROM credentials WHERE owner = $owner"
      .as[String]
      .headOption

  private def saveConsumerAndCandidatesAction(owner: String, token: String, candidates: Seq[String]) = {
    val key = generateConsumerKey(owner)
    (for {
      id <- sql"""INSERT INTO credentials(key, token, owner) VALUES ($key, $token, $owner) RETURNING id""".as[Int].head
      _ <- DBIO.sequence(
        candidates.map { name =>
          sqlu"INSERT INTO candidates(credential_id, name) VALUES ($id, $name)"
        }
      )
    } yield id).transactionally
  }

  private def consumerCandidatesAction(owner: String) =
    sql"""
         SELECT can.name 
         FROM credentials cred JOIN candidates can on cred.id = can.credential_id 
         WHERE cred.owner = $owner""".as[String]
}
