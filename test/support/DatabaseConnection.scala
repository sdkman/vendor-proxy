package support

import com.typesafe.config.ConfigFactory
import slick.driver.PostgresDriver.api._

trait DatabaseConnection {
  private lazy val config = ConfigFactory.load()

  lazy val databaseUrl = config.getString("slick.dbs.default.db.url")

  lazy val databaseUser = config.getString("slick.dbs.default.db.user")

  lazy val databasePassword = config.getString("slick.dbs.default.db.password")

  lazy val database = Database.forURL(databaseUrl, databaseUser, databasePassword)

}
