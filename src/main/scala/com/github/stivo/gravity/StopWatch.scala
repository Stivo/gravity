package com.github.stivo.gravity


import scala.collection.mutable

/**
 * Created by Stivo on 17.01.2016.
 */
object StopWatch {

  private var map: mutable.Map[String, Long] = null
  private var currentPhase: Option[String] = null

  reset()

  def start(phase: String): Unit = {
    val now: Long = finishCurrentPhaseIfNeeded()
    map += phase -> now
    currentPhase = Option(phase)
  }

  private def finishCurrentPhaseIfNeeded(): Long = {
    val now: Long = System.currentTimeMillis()
    currentPhase match {
      case Some(start) if map.contains(start) => map += start -> (now - map(start))
      case None =>
    }
    now
  }

  def finish(): mutable.Map[String, Long] = {
    finishCurrentPhaseIfNeeded()
    map
  }

  def reset(): Unit = {
    currentPhase = None
    map = mutable.LinkedHashMap.empty
  }
}