package wager

import wager.betfair.Authenticate

object Server extends App {

  val loginResponse: requests.Response = Authenticate.login()

  println(loginResponse)

}
