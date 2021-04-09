package repos

import com.google.inject.Inject
import domain.Consumer
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConsumerRepo @Inject() (val dbConfigProvider: DatabaseConfigProvider)
    extends HasDatabaseConfigProvider[JdbcProfile] {

  def persist(c: Consumer): Future[Int] = {
    db.run((for {
      id <- sql"INSERT INTO credentials(key, token, owner) VALUES (${c.id}, ${c.token}, ${c.name}) RETURNING id"
        .as[Int]
        .head
      _ <- sqlu"INSERT INTO candidates(credential_id, name) VALUES ($id, ${c.name})"
    } yield id).transactionally)
  }

  def deleteByName(name: String): Future[Int] =
    db.run((for {
      result1 <- sqlu"DELETE FROM candidates WHERE name = $name"
      result2 <- sqlu"DELETE FROM credentials WHERE owner = $name"
    } yield result1 & result2).transactionally)

  def findByKeyAndToken(key: String, token: String): Future[Seq[String]] =
    db.run(
      sql"""SELECT can.name 
            FROM credentials cred JOIN candidates can ON cred.id = can.credential_id
            WHERE cred.key = $key
            AND cred.token = $token
            ORDER BY can.name"""
        .as[String]
    )

}
