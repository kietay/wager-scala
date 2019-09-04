package wager.util

import java.io.{File, FileInputStream, InputStream}
import java.security.{KeyStore, SecureRandom}

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpExt, HttpsConnectionContext}
import wager.Settings
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.HttpCharsets._
import akka.http.scaladsl.model._
import javax.net.ssl.{KeyManager, KeyManagerFactory, SSLContext}

import scala.util.{Failure, Success, Try}

object HttpHelpers {

  def getKeyManagers(keyStoreType: String, keyStoreFile: InputStream, keyStorePassword: String)
  : Try[Array[KeyManager]] =
    Try {
      val keyStore = KeyStore.getInstance(keyStoreType)
      keyStore.load(keyStoreFile, keyStorePassword.toCharArray)
      val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
      kmf.init(keyStore, keyStorePassword.toCharArray)
      kmf.getKeyManagers
    }

  val sslContext: () => SSLContext = () => {

    val ctx = SSLContext.getInstance("TLS")

    val keyManagers =
      getKeyManagers("pkcs12", new FileInputStream(
        new File(Settings.p12CertPath)), "password"
      ) match {
        case Success(v) => v
        case Failure(ex) => println("Could not acquire KeyManager"); throw ex
      }

    ctx.init(keyManagers, null, new SecureRandom)

    ctx
  }

  case object headers {
    val accept = Accept(`application/json`)
    val acceptCharset = `Accept-Charset`(`UTF-8`, HttpCharsetRange.`*`)
    val xApplication = RawHeader("X-Application", Settings.appKey)
  }

  def httpsClient()(implicit sys: ActorSystem): HttpExt = {
    val httpsContext = new HttpsConnectionContext(sslContext())

    val http = Http()
    http.setDefaultClientHttpsContext(httpsContext)
    http
  }

}
