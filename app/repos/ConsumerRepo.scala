package repos

import com.google.inject.Inject
import domain.Consumer
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.{GetResult, JdbcProfile, SetParameter}

import java.sql.Types
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConsumerRepo @Inject() (val dbConfigProvider: DatabaseConfigProvider)
    extends HasDatabaseConfigProvider[JdbcProfile] {

  implicit def helpersSlickGetResultUUID: GetResult[UUID] =
    GetResult(r => r.nextObject.asInstanceOf[UUID])

  implicit def slickSetParameterUUID: SetParameter[UUID] =
    SetParameter { case (v, pp) => pp.setObject(v, Types.OTHER) }

  def persist(c: Consumer): Future[UUID] = {
    val credentialId = UUID.randomUUID()
    db.run((for {
      _ <- sqlu"INSERT INTO credentials(id, key, token, owner) VALUES ($credentialId, ${c.id}, ${c.token}, ${c.name})"
      _ <- sqlu"INSERT INTO candidates(credential_id, name) VALUES ($credentialId, ${c.name})"
    } yield credentialId).transactionally)
  }

  def deleteByName(name: String): Future[Int] =
    db.run((for {
      result1 <- sqlu"DELETE FROM candidates WHERE name = $name"
      result2 <- sqlu"DELETE FROM credentials WHERE owner = $name"
    } yield result1 & result2).transactionally)

  def findByKeyAndToken(key: String, token: String): Future[Option[String]] = db.run(
    sql"""SELECT can.name 
            FROM credentials cred JOIN candidates can ON cred.id = can.credential_id
            WHERE cred.key = $key
            AND cred.token = $token"""
      .as[String]
      .headOption
  )

}
