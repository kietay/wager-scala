package wager

object Settings {

  val username: String = sys.env("BETFAIR_USERNAME")
  val password: String = sys.env("BETFAIR_PASSWORD")

  val appKey: String = sys.env("BETFAIR_APP_KEY")

  val apiUrl: String = sys.env("BETFAIR_API_URL")
  val isoUrl: String = sys.env("BETFAIR_ISO_URL")

  val p12CertPath: String = sys.env("P12_CERT_PATH")
  val p12CertPassword: String = sys.env("P12_CERT_PASSWORD")

}
