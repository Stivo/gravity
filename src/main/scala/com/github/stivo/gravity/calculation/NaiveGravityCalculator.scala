package com.github.stivo.gravity.calculation

import com.github.stivo.gravity.{Point, Speed2D, Circle}
import squants.motion.{Velocity, MetersPerSecondSquared, MetersPerSecond}
import squants.time.Time

/**
 * Created by Stivo on 18.01.2016.
 */
class NaiveGravityCalculator extends GravityCalculator {

  def makeSpeed(point: Point, timePerTick: Time) = {
    val x: Velocity = MetersPerSecondSquared(point.x.toMeters) * timePerTick
    val y: Velocity = MetersPerSecondSquared(point.y.toMeters) * timePerTick
    Speed2D(x, y)
  }

  override def calculateForceVectors(circles: IndexedSeq[Circle], timePerTick: Time): IndexedSeq[Speed2D] = {
    (for (c1 <- circles)
      yield makeSpeed(circles.withFilter(c1 ne _).map(c2 => c1.gravityTo(c2)).reduce(_ + _), timePerTick)).toIndexedSeq
  }
}