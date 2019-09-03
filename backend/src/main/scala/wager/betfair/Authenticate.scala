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

import scala.util.{Success, Failure, Try}

object Authenticate extends ServiceRequests {

  private val port = 443

  // todo update http client used
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


}
