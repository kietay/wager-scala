package wager

import play.api.libs.json.{JsResult, JsValue, Reads}
import wager.betfair.Authenticate

object Server extends App {

  // val loginResponse: String = Authenticate.login().getOrElse("nop")

  Authenticate.loginWithAkka()

  // println(loginResponse)

}
