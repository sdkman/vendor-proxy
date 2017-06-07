package support

import org.flywaydb.core.Flyway

trait DbMigration {
  self: DatabaseConnection =>

  private lazy val flyway = {
    val f = new Flyway()
    f.setDataSource(databaseUrl, databaseUser, databasePassword)
    f
  }

  private [support] def reloadSchema(): Int = {
    flyway.clean()
    flyway.migrate()
  }
}