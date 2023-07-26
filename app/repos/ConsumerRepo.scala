package repos

import com.google.inject.Inject
import domain.Consumer
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.{GetResult, JdbcProfile}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ConsumerFields(candidate: String, vendor: Option[String])

class ConsumerRepo @Inject() (val dbConfigProvider: DatabaseConfigProvider)
    extends HasDatabaseConfigProvider[JdbcProfile] {

  def createOrUpdate(c: Consumer): Future[Int] = {
    db.run((for {
      id <- sql"""
             INSERT INTO credentials(key, token, owner, vendor)
             VALUES (${c.key}, ${c.token}, ${c.owner}, ${c.vendor})
             ON CONFLICT(owner) DO UPDATE SET key = ${c.key}, token = ${c.token}
             RETURNING id"""
        .as[Int]
        .head
      _ <- sqlu"DELETE FROM candidates WHERE credential_id = $id"
      _ <- DBIO.sequence(
        c.candidates.map(name => sqlu"INSERT INTO candidates(credential_id, name) VALUES ($id, $name)")
      )
    } yield id).transactionally)
  }

  def deleteByConsumerKey(key: String): Future[Int] = db.run(sqlu"DELETE FROM credentials WHERE key = $key")

  implicit val getConsumerFieldsResult: GetResult[ConsumerFields] = GetResult(r => ConsumerFields(r.<<, r.<<))

  def findConsumerFieldsByKeyAndToken(key: String, token: String): Future[Seq[ConsumerFields]] =
    db.run(
      sql"""SELECT can.name, cred.vendor
            FROM credentials cred JOIN candidates can ON cred.id = can.credential_id
            WHERE cred.key = $key
            AND cred.token = $token
            ORDER BY can.name"""
        .as[ConsumerFields]
    )

}
