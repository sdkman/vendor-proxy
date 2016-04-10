package utils

import play.api.Play
import play.api.Play.current

object Environment {

  def apiUrl(service: String) =
    Play.application.configuration
      .getString(s"services.$service.url")
      .getOrElse("invalid")

  def accessToken(service: String) =
    Play.application.configuration
      .getString(s"services.$service.accessToken")
      .getOrElse("invalid")

  def secret = Option(System.getenv("ADMIN_TOKEN")).getOrElse("default_token")

  def consumerCollection = Play.application.configuration.getString("consumers.collection").getOrElse("consumers")

  def applicationCollection = Play.application.configuration.getString("application.collection").getOrElse("application")

  def version = Play.application.configuration.getString("application.version").getOrElse("version not found")

}
