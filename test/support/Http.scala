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

import scalaj.http.Http._
import scalaj.http.{HttpException, HttpOptions, Http => HttpClient}

object Http {

  val host = "http://vendor-proxy:9000"

  def get(endpoint: String)(implicit headers: Map[String, String]): (Int, String) =
    handle(operation(HttpClient.get(s"$host$endpoint")))

  def postJson(endpoint: String, json: String)(implicit headers: Map[String, String]): (Int, String) =
    handle(operation(HttpClient.postData(s"$host$endpoint", json)))

  def putJson(endpoint: String, json: String)(implicit headers: Map[String, String]): (Int, String) =
    handle(operation(HttpClient.postData(s"$host$endpoint", json).method("PUT")))

  private def handle(request: Request): (Int, String) = {
    //nasty scalaj hack prevents multiple posts
    import scalaj.http.Http.readString
    try {
      val (rc, hm, rb) = request.asHeadersAndParse[String](readString)
      (rc, rb)
    } catch {
      case e: HttpException => {
        (e.code, e.body)
      }
    }
  }

  private def operation(request: Request)(implicit headers: Map[String, String]): Request =
    request
      .headers(
        headers
          .updated("Accept", "application/json")
          .updated("Content-Type", "application/json"))
      .option(HttpOptions.connTimeout(10000))
      .option(HttpOptions.readTimeout(10000))

}
