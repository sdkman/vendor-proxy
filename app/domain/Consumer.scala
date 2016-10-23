package domain

import play.api.libs.json.Json
import utils.TokenGenerator.{generateConsumerKey, generateSHAToken}

case class Consumer(_id: String, name: String, token: String)

object Consumer {
  implicit val consumerWrites = Json.writes[Consumer]
  implicit val consumerReads = Json.reads[Consumer]

  def fromName(name: String): Consumer =
    Consumer(_id = generateConsumerKey(name), name = name, token = generateSHAToken(name))
}