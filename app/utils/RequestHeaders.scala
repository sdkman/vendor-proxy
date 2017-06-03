package utils

trait RequestHeaders {
  val config: VendorProxyConfig

  def consumerHeader(consumer: String) = "Consumer" -> consumer

  def tokenHeader(service: String) = "Service-Token" -> config.serviceToken(service)
}
