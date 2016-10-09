package com.symbolscope.gic.predictors

import java.io.{DataInputStream, InputStream}
import java.nio.{ByteBuffer, ByteOrder}
import java.util.Scanner

object Read {

  def readSimpleArrayFiles(inputs: InputStream, events: InputStream): EventPredictionData = {
    val isaf = loadSAF(inputs)
    val esaf = loadSAF(new DataInputStream(events))
    if (esaf.sampleRate != isaf.sampleRate) {
      throw new Exception("sampling rates don't match") // TODO: resample
    }
    EventPredictionData(isaf.sampleRate, isaf.data, esaf.data.map(_(0).toInt))
  }

  def readTextFiles(inputs: InputStream, events: InputStream): EventPredictionData = {
    val si = new Scanner(inputs)
    val fs = stripHeader(si)

    val sl = readScanner(si)
    val sd = sl.map(s => s.split("\\s+").map(_.toDouble).toVector)
    val se = new Scanner(events)
    val fse = stripHeader(se)
    if (fse != fs) {
      throw new Exception("sampling rates don't match") // TODO: resample
    }
    val ed = readScanner(se).map(s => s.split("\\s+").apply(0).toInt)
    EventPredictionData(fs, sd, ed)
  }

  protected def stripHeader(sc: Scanner): Double = {
    var fs = -1.0
    var done = false
    while (!done) {
      val l = sc.nextLine()
      if (l.contains("SamplesPerSecond")) {
        fs = l.split(":")(1).toDouble
      } else if (l.startsWith("[")) {
        done = true
      }
    }
    fs
  }

  protected def readScanner(sc: Scanner): Vector[String] = {
    new Iterator[String] {
      override def hasNext: Boolean = sc.hasNextLine()

      override def next(): String = sc.nextLine()
    }.toVector
  }

  protected def readIntegers(lines: Vector[String]): Vector[Int] = {
    lines.map(s => s.split("\\s+").apply(0).toInt)
  }

  case class ArrayData(sampleRate: Double, data: Vector[Vector[Double]])

  protected def loadSAF(in: InputStream): ArrayData = {
    val b5 = new Array[Byte](5)
    val b2 = new Array[Byte](2)
    in.read(b5)
    assert(new String(b5) == "SAF02")
    in.read(b2)
    val swap = b2(0) == 1 // b2 is an unsigned short. If the first byte is a 1, source is LE
    val fs = rFloat(in, swap)
    in.read(b2) // this should be a number of dimensions, but it's always 2
    val shape = new Array[Int](2)
    shape(0) = rInt(in, swap)
    shape(1) = rInt(in, swap)
    val data = (0 until shape(0)).map(i =>
      (0 until shape(1)).map(j => rFloat(in, swap)).toVector
    ).toVector
    ArrayData(fs, data)
  }

  protected def rFloat(in: InputStream, swap: Boolean): Double = {
    fourBytes(in, swap).getFloat().toDouble
  }

  protected def rInt(in: InputStream, swap: Boolean): Int = {
    fourBytes(in, swap).getInt()
  }

  protected def fourBytes(in: InputStream, swap: Boolean): ByteBuffer = {
    val b4 = new Array[Byte](4)
    in.read(b4)
    val bb = ByteBuffer.wrap(b4)
    if (swap) {
      bb.order(ByteOrder.LITTLE_ENDIAN)
    }
    bb
  }
}


