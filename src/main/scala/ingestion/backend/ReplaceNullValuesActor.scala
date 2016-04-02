package ingestion.backend

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.util.Timeout
import ingestion.IngestionRestService.{RedisResults, RedisResultsRequest, ReplaceNullValuesRequest, ReplaceNullValuesResponse}
import org.json4s.JsonAST.JValue
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.concurrent.duration._

/**
  * Created by colm on 02/04/16.
  */

object ReplaceNullValuesActor {

  def props(): Props = Props(classOf[ReplaceNullValuesActor])
}

class ReplaceNullValuesActor extends Actor with ActorLogging {

  implicit lazy val system = ActorSystem()
  implicit lazy val timeout = Timeout(15 seconds)

  var replacementValue: String = ""

  def receive = {
    case rnvr: ReplaceNullValuesRequest =>
      log.info("ReplaceNullValuesActor - received ReplaceNullValuesRequest")
      replacementValue = rnvr.replacementValue
      val mySender = sender
      context.actorOf(RedisClientActor.props, "myRedisActor") ! RedisResultsRequest(mySender)

    case rr: RedisResults =>
      log.info("ReplaceNullValuesActor - received RedisResults")
      rr.sender ! ReplaceNullValuesResponse(replaceNullValues(rr.results, replacementValue).length)
  }

  private def replaceNullValues(results: String, replacementValue: String): List[String] = {
    val resultsAST = parse(results)
    for {
      JObject(child) <- resultsAST
      JField(x, JValue) <- child
//      if y == "Avon"
    } yield x

  }

}