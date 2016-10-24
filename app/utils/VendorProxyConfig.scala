package utils

import com.google.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class VendorProxyConfig @Inject()(val configuration: Configuration) {

  def apiUrl(service: String) = configuration.getString(s"services.$service.url").getOrElse("invalid")

  def accessToken(service: String) = configuration.getString(s"services.$service.accessToken").getOrElse("invalid")

  def secret = Option(System.getenv("ADMIN_TOKEN")).getOrElse("default_token")

  def consumersTable = configuration.getString("consumers.table").getOrElse("consumers")

  def version = configuration.getString("application.version").getOrElse("version not found")
}
