package io.torchbearer.turkservice

import io.torchbearer.turkservice.filters.AverageRectangleReducer

/**
  * Created by fredricvollmer on 10/30/16.
  */
object Constants {
  // SQS
  final val SQS_HIT_COMPLETION_URL = "https://sqs.us-west-2.amazonaws.com/814009652816/completed-hits"
  final val SQS_PROCESSING_URL = "https://sqs.us-west-2.amazonaws.com/814009652816/hit-service_processing"

  // Turk Questions
  final val EXTERNAL_QUESTION_BASE_URL = "https://turkservice.torchbearer.io/question"
  final val INITIAL_ASSIGNMENT_COUNT = 1
  final val INITIAL_HIT_LIFETIME = 10000

  final val SALIENCY_QUESTION_VERSION = 1
  final val DESCRIPTION_QUESTION_VERSION = 1

  // Rectangle processing
  lazy final val DEFAULT_RECTANGLE_REDUCER = AverageRectangleReducer

  // Hit types
  final val SALIENCY_HIT_TYPE_ID = "38JF4YFDOJV9QBAKPN4AXTG4SOZ699"
  final val DESCRIPTION_HIT_TYPE_ID = "xxx"
}
