package com.github.stivo.gravity

/**
 * Created by Stivo on 17.01.2016.
 */
class FrameCounter {

  var ticks: Vector[Long] = Vector.empty

  addTick()

  val keepLastSeconds = 5

  def addTick(): Unit = {
    val millis = System.currentTimeMillis()
    ticks = ticks :+ millis
    dropOldTicks(millis)
  }

  def dropOldTicks(millis: Long): Unit = {
    val startToKeep = millis - keepLastSeconds * 1000
    ticks = ticks.dropWhile(_ < startToKeep)
  }

  def getTicks(seconds: Int = 1): Int = {
    val last: Long = ticks.last
    val start = last - seconds * 1000
    ticks.size - ticks.lastIndexWhere(_ < start)
  }

}
