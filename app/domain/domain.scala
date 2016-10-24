package domain

import play.api.libs.json.Json
import utils.TokenGenerator.{generateConsumerKey, generateSHAToken}

case class Consumer(id: String, name: String, token: String)

object Consumerz {
  implicit val consumerWrites = Json.writes[Consumer]
  implicit val consumerReads = Json.reads[Consumer]

  def fromName(name: String): Consumer =
    Consumer(
      id = generateConsumerKey(name),
      name = name,
      token = generateSHAToken(name))
}