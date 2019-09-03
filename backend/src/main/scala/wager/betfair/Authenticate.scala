package wager.betfair

import wager.Settings

object Authenticate extends ServiceRequests {

  def login(): requests.Response = {
    loginRequest(Settings.username, Settings.password)
  }

}
