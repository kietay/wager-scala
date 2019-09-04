package wager.betfair

import wager.Settings
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.ssl.{SSLSocketFactory, StrictHostnameVerifier}
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import javax.net.ssl.KeyManager
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.util

import akka.http.scaladsl.HttpsConnectionContext
import akka.util.ByteString
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import wager.util.JsonSupport
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.unmarshalling.{FromResponseUnmarshaller, Unmarshal}
import akka.stream.ActorMaterializer
import HttpCharsets._
import HttpMethods._
import MediaTypes._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.unmarshalling._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import spray.json._

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}



object Authenticate extends JsonSupport {

  private val port = 443

  def login(): Try[String] = {

    val httpClient: DefaultHttpClient = new DefaultHttpClient()
    val ctx = SSLContext.getInstance("TLS")

    val keyManagers =
      getKeyManagers("pkcs12", new FileInputStream(
        new File("/Users/kieran/projects/wager/client-2048.p12")), "password"
      ) match {
        case Success(v) => v
        case Failure(ex) => println("Could not acquire KeyManager"); throw ex
      }

    ctx.init(keyManagers, null, new SecureRandom)

    val factory = new SSLSocketFactory(ctx, new StrictHostnameVerifier)
    val manager = httpClient.getConnectionManager
    manager.getSchemeRegistry.register(new Scheme("https", port, factory))

    val httpPost = new HttpPost(Settings.isoUrl)

    val nvps = new util.ArrayList[NameValuePair]
    nvps.add(new BasicNameValuePair("username", Settings.username))
    nvps.add(new BasicNameValuePair("password", Settings.password))
    httpPost.setEntity(new UrlEncodedFormEntity(nvps))
    httpPost.setHeader("X-Application", Settings.appKey)

    val response = httpClient.execute(httpPost)
    val entity = response.getEntity

    if (entity != null)
      Success(EntityUtils.toString(entity))
    else
      Failure(new Exception("Did not receive expected response from https login attempt"))
  }

  private def getKeyManagers(keyStoreType: String, keyStoreFile: InputStream, keyStorePassword: String)
  : Try[Array[KeyManager]] =
    Try {
      val keyStore = KeyStore.getInstance(keyStoreType)
      keyStore.load(keyStoreFile, keyStorePassword.toCharArray)
      val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
      kmf.init(keyStore, keyStorePassword.toCharArray)
      kmf.getKeyManagers
    }

  case class LoginRequest(username: String, password: String)

  case class LoginResponse(sessionToken: String, loginStatus: String)

  def loginWithAkka(): Try[String] = {

    object LoginResponseFormat extends DefaultJsonProtocol with SprayJsonSupport {
      implicit val loginResponseFormat: RootJsonFormat[LoginResponse] = jsonFormat2(LoginResponse)
    }

    import LoginResponseFormat._

    implicit val loginResponseUnmarshaller: Unmarshaller[JsValue, LoginResponse] =
      Unmarshaller.strict(jsValue => loginResponseFormat.read(jsValue))

    implicit val system: ActorSystem = ActorSystem("Login-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    implicit val executionContext: ExecutionContext = system.dispatcher

    val accept = Accept(`application/json`)
    val acceptCharset = `Accept-Charset`(`UTF-8`, HttpCharsetRange.`*`)
    val xApplication = RawHeader("X-Application", Settings.appKey)

    val headers = Seq(accept, acceptCharset, xApplication)

    val httpsContext = new HttpsConnectionContext(sslContext())

    val http = Http()
    http.setDefaultClientHttpsContext(httpsContext)

    val responses = for {
      res <- Http().singleRequest(HttpRequest(
             POST, uri = Settings.isoUrl,
             entity = FormData(Map("username" -> Settings.username, "password" -> Settings.password)).toEntity(HttpCharsets.`UTF-8`),
             headers = headers.toList
      ))
    } yield Unmarshal(res).to[LoginResponse]

    val x = Await.result(responses, 10.seconds)

    println(x)

    Success("Donezo")

  }

  val sslContext: () => SSLContext = () => {

    val ctx = SSLContext.getInstance("TLS")

    val keyManagers =
      getKeyManagers("pkcs12", new FileInputStream(
        new File("/Users/kieran/projects/wager/client-2048.p12")), "password"
      ) match {
        case Success(v) => v
        case Failure(ex) => println("Could not acquire KeyManager"); throw ex
      }

    ctx.init(keyManagers, null, new SecureRandom)

    ctx
  }

}
