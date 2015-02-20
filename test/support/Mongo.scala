/**
 * Copyright 2014 Marco Vermeulen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package support

import com.mongodb.casbah.Imports._
import utils.TokenGenerator.generateMD5Token

object Mongo {

  lazy val mongoClient = MongoClient()

  def primeDatabase(name: String): MongoDB = mongoClient(name)

  def createCollection(db: MongoDB, name: String): MongoCollection = db(name)

  def dropCollection(coll: MongoCollection) = coll.drop()

  def vendorExists(coll: MongoCollection, vendor: String): Boolean =
    coll.findOne(MongoDBObject("name" -> vendor)).isDefined

  def vendorConsumerKey(coll: MongoCollection, vendor: String): Option[String] =
    coll.findOne(MongoDBObject("name" -> vendor)).map(v => v.getAs[String]("_id").get)

  def vendorConsumerToken(coll: MongoCollection, vendor: String): Option[String] =
    coll.findOne(MongoDBObject("name" -> vendor)).map(v => v.getAs[String]("token").get)
  
  def saveVendor(coll: MongoCollection, name: String, token: String) =
    coll.save(MongoDBObject("_id" -> generateMD5Token(name), "token" -> token, "name" -> name))

}