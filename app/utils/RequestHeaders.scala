package utils

trait RequestHeaders {
  val config: VendorProxyConfig

  def consumerHeader(consumer: String) = "consumer" -> consumer

  def tokenHeader(service: String) = "access_token" -> config.accessToken(service)
}
