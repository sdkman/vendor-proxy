package utils

import com.google.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class VendorProxyConfig @Inject() (val configuration: Configuration) {

  def apiUrl(service: String) = configuration.get[String](s"services.$service.url")

  def serviceToken(service: String) = configuration.get[String](s"services.$service.serviceToken")

  def secret = configuration.get[String]("admin.token")
}
