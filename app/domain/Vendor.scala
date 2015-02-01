package domain

import play.api.libs.json.Json

case class Vendor(name: String, token: String)

object Vendor {
  implicit val userFormat = Json.format[Vendor]
}