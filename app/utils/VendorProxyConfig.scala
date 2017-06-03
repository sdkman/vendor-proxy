package utils

import com.google.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class VendorProxyConfig @Inject()(val configuration: Configuration) {

  def apiUrl(service: String) = configuration.getString(s"services.$service.url").getOrElse("invalid")

  def serviceToken(service: String) = configuration.getString(s"services.$service.serviceToken").getOrElse("invalid")

  def secret = configuration.getString("admin.token").getOrElse("invalid")

  def consumersTable = configuration.getString("consumers.table").getOrElse("consumers")

  def version = configuration.getString("application.version").getOrElse("version not found")
}
