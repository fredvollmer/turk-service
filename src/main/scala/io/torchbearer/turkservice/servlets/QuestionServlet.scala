package io.torchbearer.turkservice.servlets

import akka.actor.ActorSystem
import io.torchbearer.ServiceCore.DataModel.ExecutionPoint._
import io.torchbearer.turkservice.TurkServiceStack
import io.torchbearer.turkservice.nlp.DescriptorResult
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.{AsyncResult, CorsSupport, ErrorHandler, FutureSupport}
import io.torchbearer.turkservice.nlp.QueryBuilder._

import scala.concurrent.{ExecutionContext, Future}

class QuestionServlet(system: ActorSystem) extends TurkServiceStack with FutureSupport with CorsSupport
  with JacksonJsonSupport {

  override protected implicit def executor: ExecutionContext = system.dispatcher

  protected implicit lazy val jsonFormats: Formats = DefaultFormats


  /**
    * Respond to preflight requests
    */
  options("/*") {
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"))
    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS")
  }

  /** ******** Object Sampling ***********/

  get("/objectsampling") {
    val assignmentId = params('assignmentId)
    val lat = params('lat).toFloat
    val long = params('long).toFloat
    val bearing = params('bearing).toInt

    new AsyncResult() {
      val is = Future {
        contentType = "text/html"
        mustache("/objectSampling.mustache",
          "instruction" -> "first right turn",
          "assignmentId" -> assignmentId,
          "lat" -> lat,
          "long" -> long,
          "bearing" -> bearing)
      }
    }
  }

  /** ******** Object Description ***********/

  get("/objectdescription") {
    val assignmentId = params('assignmentId)
    val lat = params('lat).toFloat
    val long = params('long).toFloat
    val bearing = params('bearing).toInt

    new AsyncResult() {
      val is = Future {
        contentType = "text/html"
        mustache("/objectDescription.mustache",
          "instruction" -> "first right turn",
          "assignmentId" -> assignmentId,
          "lat" -> lat,
          "long" -> long,
          "bearing" -> bearing)
      }
    }
  }

  post("/objectdescription/next") {
    val results = parsedBody.extract[List[DescriptorResult]]

    new AsyncResult() {
      val is = Future {
        contentType = formats("json")
        getNextQuestion(results)
      }
    }
  }

  override def error(handler: ErrorHandler): Unit = ???
}
