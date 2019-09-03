package wager

import wager.betfair.Authenticate

object Server extends App {

  val loginResponse: String = Authenticate.login().getOrElse("Could not login")

  println(loginResponse)

}
