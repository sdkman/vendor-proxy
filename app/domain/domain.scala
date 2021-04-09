package domain

import play.api.libs.json.Json
import utils.TokenGenerator.{generateConsumerKey, generateSHAToken}

case class Consumer(key: String, owner: String, token: String, candidates: Seq[String])

object Consumers {
  implicit val consumerWrites = Json.writes[Consumer]
  implicit val consumerReads  = Json.reads[Consumer]

  def fromOwner(owner: String, candidates: Seq[String]): Consumer =
    Consumer(key = generateConsumerKey(owner), owner = owner, token = generateSHAToken(owner), candidates)
}
