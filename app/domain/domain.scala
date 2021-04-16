package domain

import play.api.libs.json.{Json, Writes, Reads}
import utils.TokenGenerator.{generateConsumerKey, generateSHAToken}

case class Consumer(key: String, owner: String, token: String, candidates: Seq[String], vendor: Option[String])

object Consumers {
  implicit val consumerWrites: Writes[Consumer] = Json.writes[Consumer]
  implicit val consumerReads: Reads[Consumer]   = Json.reads[Consumer]

  def fromOwner(owner: String, candidates: Seq[String], vendor: Option[String]): Consumer =
    Consumer(key = generateConsumerKey(owner), owner = owner, token = generateSHAToken(owner), candidates, vendor)
}
