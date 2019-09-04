package wager

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import wager.services.Betfair

import scala.concurrent.{ExecutionContext, Await}
import scala.concurrent.duration._

/**
  * Currently used to execute test code during development
  */
object Server extends App {

  implicit val system: ActorSystem = ActorSystem("wager")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val executionContext: ExecutionContext = system.dispatcher

  val testRes = Betfair.login()

  println(Await.result(testRes, 10.seconds))

}
