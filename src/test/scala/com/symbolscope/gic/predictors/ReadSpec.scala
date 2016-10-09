package com.symbolscope.gic.predictors

import org.scalatest.{FlatSpec, Matchers}


class ReadSpec extends FlatSpec with Matchers {
  "A Read instance" should "read EventPredictionData from text files" in {
    val epd = Read.readTextFiles(
      getClass.getResourceAsStream("stim.txt"),
      getClass.getResourceAsStream("spikes.txt")
    )
    epd.sampleRate should be (20000)
    epd.inputs.size should be (1000340)
    epd.events.size should be (3836)
    epd.inputs(0).size should be (1)
    epd.inputs(0)(0) should be (0.0114048625686)
  }

  it should "read EventPredictionData from simple array files" in {
    val epd = Read.readSimpleArrayFiles(
      getClass.getResourceAsStream("stim.saf"),
      getClass.getResourceAsStream("spikes.saf")
    )
    epd.sampleRate should be (20000)
    epd.inputs.size should be (1000340)
    epd.events.size should be (3836)
    epd.inputs(0).size should be (1)
    epd.inputs(0)(0) should be (0.011404862627387047)

  }
}