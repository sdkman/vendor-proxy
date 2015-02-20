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
import scalaj.http.{Http => HttpClient, HttpException, HttpOptions}

object Http {

  val host = "http://localhost:9000"

  def get(endpoint: String, tokenHeader: (String, String)): (Int, String) = {
    handle {
      HttpClient(s"$host$endpoint")
        .headers(
          tokenHeader,
          "Accept" -> "application/json",
          "Content-Type" -> "application/json")
        .option(HttpOptions.connTimeout(1000))
        .option(HttpOptions.readTimeout(5000))
    }
  }

  def postJson(endpoint: String, json: String, tokenHeader: (String, String)): (Int, String) = {
    handle {
      HttpClient.postData(s"$host$endpoint", json)
        .headers(
          tokenHeader,
          "Accept" -> "application/json",
          "Content-Type" -> "application/json"
        )
        .option(HttpOptions.connTimeout(1000))
        .option(HttpOptions.readTimeout(5000))
    }
  }

  def putJson(endpoint: String, json: String, tokenHeader: (String, String)): (Int, String) = {
    handle {
      HttpClient.postData(s"$host$endpoint", json)
        .method("PUT")
        .headers(
          tokenHeader,
          "Accept" -> "application/json",
          "Content-Type" -> "application/json"
        )
        .option(HttpOptions.connTimeout(1000))
        .option(HttpOptions.readTimeout(5000))
    }
  }

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
}
