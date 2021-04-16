package controllers

import com.google.inject.Inject
import play.api.Logging
import play.api.libs.ws.WSClient
import play.api.mvc.InjectedController
import repos.ConsumerRepo
import security.AsConsumer
import utils.{RequestHeaders, VendorProxyConfig}

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc.ControllerComponents
import play.api.mvc.AbstractController

class ProxyController @Inject() (
    val config: VendorProxyConfig,
    val wSClient: WSClient,
    val cc: ControllerComponents,
    implicit val cr: ConsumerRepo
) extends AbstractController(cc)
    with RequestHeaders
    with Logging {

  def execute(service: String) =
    AsConsumer(parse.json, controllerComponents.actionBuilder) {
      (request, candidates: String, vendor: Option[String]) =>
        logger.info(s"Proxy request for: $service with candidates: $candidates and vendor: $vendor")
        val headers = Seq(tokenHeader(service), candidatesHeader(candidates)) ++ vendor.map(vendorHeader)
        wSClient
          .url(config.apiUrl(service))
          .withHttpHeaders(headers: _*)
          .withMethod(request.method)
          .withBody(request.body)
          .execute()
          .map(response => Status(response.status)(response.body))
    }
}
