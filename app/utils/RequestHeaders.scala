package utils

trait RequestHeaders {
  val config: VendorProxyConfig

  def candidatesHeader(candidates: String) = "Candidates" -> candidates

  def tokenHeader(service: String) = "Service-Token" -> config.serviceToken(service)
}
