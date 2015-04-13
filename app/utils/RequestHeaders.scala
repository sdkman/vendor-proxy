package utils

trait RequestHeaders {
  def consumerHeader(consumer: String) = "consumer" -> consumer

  def tokenHeader(service: String) = "access_token" -> Environment.accessToken(service)
}
