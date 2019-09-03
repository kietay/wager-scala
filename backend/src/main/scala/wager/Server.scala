package wager

import play.api.libs.json.{JsResult, JsValue, Reads}
import wager.betfair.{Authenticate, LoginResponse}

object Server extends App {

  // val loginResponse: String = Authenticate.loginWithAkka()

  Authenticate.loginWithAkka()

  // println(loginResponse)

}
