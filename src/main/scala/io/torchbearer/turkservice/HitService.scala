package io.torchbearer.turkservice

import io.torchbearer.ServiceCore.DataModel.ExecutionPoint
import io.torchbearer.ServiceCore.Utils._
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

/**
  * Created by fredricvollmer on 11/11/16.
  */
object HitService {
  private val turkClient = TurkClientFactory.getClient

  private def getHitReward(t: Task): Double = {
    t match {
      case SALIENCY_DETECTION => 0.05
      case OBJECT_DESCRIPTION => 0.05
      case _ => 0.05
    }
  }

  private def submitHitForExecutionPoint(ep: ExecutionPoint, t: Task, n: Int, reward: Double, lifetime: Long) {
    val baseUrl = s"${Constants.EXTERNAL_QUESTION_BASE_URL}/${t.id}"
    val url = formatURLWithQueryParams(baseUrl,
      "epId" -> ep.executionPointId,
      "lat" -> ep.lat,
      "long" -> ep.long,
      "bearing" -> ep.bearing
    )

    val questionXML =
      <ExternalQuestion xmlns="http://mechanicalturk.amazonaws.com/AWSMechanicalTurkDataSchemas/2006-07-14/ExternalQuestion.xsd">
        <ExternalURL>{url}</ExternalURL>
        <FrameHeight>700</FrameHeight>
      </ExternalQuestion>
    val question = questionXML.toString()
    val hit = turkClient.createHIT(null, t.title, t.description, t.keywords, question, reward, t.assingmentDuration, t.autoApprovalDelay, lifetime, n, null, t.qualificationRequirements, null)

    println(s"Created ${t.name} hit for execution point ${ep.executionPointId}. (${hit.getHITId})")
    // For now, don't bother adding this hit to DB
  }

  def processExecutionPoints(eps: Seq[ExecutionPoint]): Unit = {
    println(s"Processing ${eps.length} execution points...")
    eps.par.foreach(ep => {
      submitHitForExecutionPoint(ep, SALIENCY_DETECTION, Constants.INITIAL_ASSIGNMENT_COUNT, getHitReward(SALIENCY_DETECTION), Constants.INITIAL_HIT_LIFETIME)
    })
    println("Processing complete")
  }
}
