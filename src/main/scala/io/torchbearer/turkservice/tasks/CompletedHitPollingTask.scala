package io.torchbearer.turkservice.tasks

import akka.actor.ActorSystem
import com.amazonaws.services.sqs.model.{DeleteMessageRequest, Message, ReceiveMessageRequest}
import io.torchbearer.ServiceCore.AWSServices.SQS
import io.torchbearer.ServiceCore.DataModel.{ObjectDescriptionAssignment, SaliencyAssignment}
import io.torchbearer.ServiceCore.tyoes.Rectangle
import io.torchbearer.turkservice.SaliencyResultProcessor._
import io.torchbearer.turkservice.{Constants, TurkClientFactory}
import org.json4s.DefaultFormats
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.concurrent.{ExecutionContext, Future}
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

/**
  * Created by fredricvollmer on 10/30/16.
  */
class CompletedHitPollingTask(system: ActorSystem) extends Runnable {
  protected implicit def executor: ExecutionContext = system.dispatcher
  implicit val formats = DefaultFormats

  private val logger = LoggerFactory.getLogger(this.getClass)
  private val turkClient = TurkClientFactory.getClient

  // Build SQS client
  private val sqsClient = SQS.getClient
  private val request = new ReceiveMessageRequest(Constants.SQS_HIT_COMPLETION_URL)
  request.setWaitTimeSeconds(20)

  logger.debug("SQS client built for hit completion.")

  override def run(): Unit = {
    while (true) {
      processSQSMessages()
    }
  }

  private def processSQSMessages(): Unit = {
    log("Polling task running: HITS.")
    val messages: Seq[Message] = sqsClient.receiveMessage(request).getMessages
    log(s"Received ${messages.length} hit completion messages")

    messages.foreach((m: Message) => {
      val handle = m.getReceiptHandle
      val hitID = (parse(m.getBody) \ "HITId").extract[String]
      val hitTypeId = (parse(m.getBody) \ "HITTypeId").extract[String]

      Future {
        val turkAssignemnts = turkClient.getAllAssignmentsForHIT(hitID)

        hitTypeId match {
          case Constants.DESCRIPTION_HIT_TYPE_ID => {
            val assignments = turkAssignemnts.map(m => new ObjectDescriptionAssignment(m.getAssignmentId, hitID, 0,
              Some(m.getAnswer), Some(m.getWorkerId), None))
          }
          case Constants.SALIENCY_HIT_TYPE_ID => {
            val assignments = turkAssignemnts.map(m => new SaliencyAssignment(m.getAssignmentId,
              hitID, 0, Some(Rectangle(m.getAnswer)), Some(m.getWorkerId), None))
            processSaliencyAssignemnts(assignments.toList)
          }
        }
      }

      // Delete message from queue
      Future {
        val deleteReq = new DeleteMessageRequest(Constants.SQS_HIT_COMPLETION_URL, handle)
        sqsClient.deleteMessage(deleteReq)
      }
    })

    log("Polling task complete.")
  }

  private def log(message: String) = {
    println("PollingService: " + message)
  }
}
