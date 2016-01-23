package com.github.stivo.gravity


import scala.collection.immutable.ListMap
import scala.collection.mutable

/**
 * Created by Stivo on 17.01.2016.
 */
object StopWatch {

  private var phaseStarts: mutable.Map[String, Long] = null
  private var currentResults: mutable.Map[String, String] = null
  private var lastResults: Vector[(String, String)] = Vector.empty
  private var currentPhase: Option[String] = null

  reset()

  def start(phase: String): Unit = {
    val now: Long = finishCurrentPhaseIfNeeded()
    phaseStarts += phase -> now
    currentPhase = Option(phase)
  }

  def addEntry(phase: String, value: String): Unit = {
    currentResults += phase -> value
  }

  private def finishCurrentPhaseIfNeeded(): Long = {
    val now: Long = System.currentTimeMillis()
    currentPhase match {
      case Some(start) if phaseStarts.contains(start) =>
        currentResults += start -> ((now - phaseStarts(start)) + " ms")
      case None =>
    }
    now
  }

  def finish(): Unit = {
    finishCurrentPhaseIfNeeded()
    lastResults = currentResults.toVector
  }

  def lastResult() = lastResults

  def reset(): Unit = {
    currentPhase = None
    phaseStarts = mutable.HashMap.empty
    currentResults = mutable.LinkedHashMap.empty
  }
}
