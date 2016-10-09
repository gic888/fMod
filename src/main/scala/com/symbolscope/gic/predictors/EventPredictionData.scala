package com.symbolscope.gic.predictors

/**
  * Data for event prediction.
  */
case class EventPredictionData(sampleRate: Double, inputs: Vector[Vector[Double]], events: Vector[Int]) {

}
