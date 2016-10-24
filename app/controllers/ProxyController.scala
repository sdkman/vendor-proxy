package controllers

import com.google.inject.Inject
import play.api.Logger
import play.api.libs.ws.WSClient
import play.api.mvc.Controller
import repos.ConsumerRepo
import security.AsConsumer
import utils.{RequestHeaders, VendorProxyConfig}

import scala.concurrent.ExecutionContext.Implicits.global

class ProxyController @Inject()(val config: VendorProxyConfig, wSClient: WSClient, implicit val cr: ConsumerRepo)
  extends Controller
    with RequestHeaders {

  def execute(service: String) = AsConsumer(parse.json) { (request, consumerName) =>
    Logger.info(s"Proxying $service on behalf of $consumerName")
    wSClient.url(config.apiUrl(service))
      .withHeaders(tokenHeader(service), consumerHeader(consumerName))
      .withMethod(request.method)
      .withBody(request.body)
      .execute()
      .map(response => Status(response.status)(response.body))
  }
}
