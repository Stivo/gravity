package com.github.stivo.gravity.calculation

import com.github.stivo.gravity.{Point, Speed2D, Circle}
import squants.motion.{Velocity, MetersPerSecondSquared, MetersPerSecond}
import squants.time.Time
import scala.collection.optimizer._

/**
 * Created by Stivo on 18.01.2016.
 */
class NaiveGravityCalculator(parallel: Boolean = true) extends GravityCalculator {

  def makeSpeed(point: Point, timePerTick: Time) = {
    val x: Velocity = MetersPerSecondSquared(point.x.toMeters) * timePerTick
    val y: Velocity = MetersPerSecondSquared(point.y.toMeters) * timePerTick
    Speed2D(x, y)
  }

  override def calculateForceVectorsForMoreThanOne(circles: IndexedSeq[Circle], timePerTick: Time): IndexedSeq[Speed2D] = {
    val circles1 = if (parallel) {
      circles.par
    } else {
      circles
    }
    (for (c1 <- circles1)
      yield makeSpeed(circles.withFilter(c1 ne _).map(c2 => c1.gravityTo(c2)).reduce(_ + _), timePerTick)).toIndexedSeq
  }

  override def toString: String = "Naive"+ (if (parallel) " (parallel)" else "")
}
