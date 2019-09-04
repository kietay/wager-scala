package wager.services

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{HttpRequest, _}
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.ActorMaterializer
import spray.json.{DefaultJsonProtocol, RootJsonFormat, _}
import wager.Settings
import wager.domain._
import wager.util.HttpHelpers
import wager.util.HttpHelpers.headers._

import scala.concurrent._


/**
  * Make service calls to Betfair
  */
object Betfair {

  object LoginResponseHandler extends DefaultJsonProtocol with SprayJsonSupport {

    implicit val loginResponseFormat: RootJsonFormat[BetfairLoginResponse] = jsonFormat2(BetfairLoginResponse)

    implicit val loginResponseUnmarshaller: Unmarshaller[JsValue, BetfairLoginResponse] =
      Unmarshaller.strict(jsValue => loginResponseFormat.read(jsValue))

  }

  def login()(implicit sys: ActorSystem, mat: ActorMaterializer, ctx: ExecutionContext): Future[BetfairLoginResponse] = {

    import LoginResponseHandler._

    val headers = Seq(accept, acceptCharset, xApplication)
    val form = FormData(Map(
      "username" -> Settings.username,
      "password" -> Settings.password
    )).toEntity

    for {
      res <- HttpHelpers.httpsClient().singleRequest(HttpRequest(
        POST, uri = Settings.isoUrl,
        entity = form,
        headers = headers.toList
      ))
      ent <- Unmarshal(res).to[BetfairLoginResponse]
    }
      yield ent

  }
}
