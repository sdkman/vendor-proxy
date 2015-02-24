package utils

import play.api.Play
import play.api.Play.current

trait Environment {

  def apiUrl(service: String) =
    Play.application.configuration.getString(s"services.$service.url").getOrElse("invalid")

  def tokenHeader(service: String) = "access_token" -> accessToken(service)

  private def accessToken(service: String) =
    Play.application.configuration.getString(s"services.$service.accessToken").getOrElse("invalid")
  
}
