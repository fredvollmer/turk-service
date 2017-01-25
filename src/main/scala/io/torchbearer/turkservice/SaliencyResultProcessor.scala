package io.torchbearer.turkservice
import io.torchbearer.ServiceCore.DataModel.SaliencyAssignment
import io.torchbearer.ServiceCore.DataModel.SaliencyAssignment._
import io.torchbearer.turkservice.filters.AverageRectangleReducer

/**
  * Created by fredvollmer on 1/22/17.
  */

object SaliencyResultProcessor {

  def processSaliencyAssignemnts(assignments: List[SaliencyAssignment]): Unit = {
      val rects = assignments.flatMap(_.rectangle)
      val filters = List(
        new AverageRectangleReducer(rects)
      )

    // Save assignments to DB
    insertSaliencyAssignments(assignments)

    // Run results through filters


  }
}
