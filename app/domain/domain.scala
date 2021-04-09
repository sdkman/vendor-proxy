package domain

import play.api.libs.json.Json
import utils.TokenGenerator.{generateConsumerKey, generateSHAToken}

case class Consumer(key: String, name: String, token: String)

object Consumers {
  implicit val consumerWrites = Json.writes[Consumer]
  implicit val consumerReads  = Json.reads[Consumer]

  def fromName(name: String): Consumer =
    Consumer(key = generateConsumerKey(name), name = name, token = generateSHAToken(name))
}
