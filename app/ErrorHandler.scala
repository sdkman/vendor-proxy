import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent._
import javax.inject.Singleton

import play.libs.Json
import utils.ErrorMarshalling

@Singleton
class ErrorHandler extends HttpErrorHandler with ErrorMarshalling {
  def onClientError(request: RequestHeader, statusCode: Int, message: String) =
    Future.successful(Status(statusCode)(clientError(statusCode, s"A client error occurred: $message")))

  def onServerError(request: RequestHeader, exception: Throwable) =
    Future.successful(Status(500)(internalServerError(exception)))
}