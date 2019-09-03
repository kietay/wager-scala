package wager.betfair

import wager.Settings

class ServiceRequests() {

  type Header = (String, String)

  val accept: Header = "content-type" -> "application/json"
  val acceptCharset: Header = "Accept-Charset" -> "utf-8, iso-8859-1;q=0.5, *;q=0.1"
  val xApplication: Header = "X-Application" -> Settings.appKey

  case class LoginResponse(token: String, product: String, status: String, error: String)

  def loginRequest(username: String, password: String): requests.Response = {

    val headers = Map(accept, acceptCharset, xApplication)

    val requestUrl = s"${Settings.isoUrl}/login?username=$username&password=$password"

    requests.post(
      requestUrl,
      headers=headers
    )
  }

}
