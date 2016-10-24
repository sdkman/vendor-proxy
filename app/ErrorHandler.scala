import javax.inject.Singleton

import play.api.http.HttpErrorHandler
import play.api.mvc.Results._
import play.api.mvc._
import utils.ErrorMarshalling

import scala.concurrent._

@Singleton
class ErrorHandler extends HttpErrorHandler with ErrorMarshalling {
  def onClientError(request: RequestHeader, statusCode: Int, message: String) =
    Future.successful(Status(statusCode)(clientError(statusCode, s"A client error occurred: $message")))

  def onServerError(request: RequestHeader, exception: Throwable) =
    Future.successful(Status(500)(internalServerErrorMsg(exception)))
}