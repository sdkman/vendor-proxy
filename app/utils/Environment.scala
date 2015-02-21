package utils

trait Environment {

  def releaseApiUrl = Option(System.getenv("RELEASE_API_URL")).getOrElse("http://localhost:8080/release")

  def releaseAccessToken = Option(System.getenv("RELEASE_ACCESS_TOKEN")).getOrElse("release_token")
  
}
