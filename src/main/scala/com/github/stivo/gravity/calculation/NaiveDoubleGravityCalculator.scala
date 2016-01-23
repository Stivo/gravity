package com.github.stivo.gravity.calculation

import com.github.stivo.gravity.{Geometry, Point, Speed2D, Circle}
import squants.motion.{Velocity, MetersPerSecondSquared, MetersPerSecond}
import squants.time.Time
import scala.collection.optimizer._

/**
 * Created by Stivo on 18.01.2016.
 */
class NaiveDoubleGravityCalculator(parallel: Boolean = true) extends GravityCalculator {

  def makeSpeed(point: (Double, Double), timePerTick: Time) = {
    val x: Velocity = MetersPerSecondSquared(point._1) * timePerTick
    val y: Velocity = MetersPerSecondSquared(point._2) * timePerTick
    Speed2D(x, y)
  }

  def addTuples(t1: (Double, Double), t2: (Double, Double)) = (t1._1 + t2._1, t1._2 + t2._2)

  override def calculateForceVectorsForMoreThanOne(circles: IndexedSeq[Circle], timePerTick: Time): IndexedSeq[Speed2D] = {
    val circles1 = if (parallel) circles.par else circles
    val fastCircles =
      for (c1 <- circles1)
        yield new FastCircle(c1.center.x.toMeters, c1.center.y.toMeters, c1.mass.toKilograms)

    (for (c1 <- fastCircles)
      yield makeSpeed(fastCircles.filter(c1 ne _).map(c2 => c1.gravityTo(c2)).reduce(addTuples), timePerTick)).toIndexedSeq
  }

  class FastCircle(val x: Double, val y: Double, val mass: Double) {
    def distanceTo(other: FastCircle): Double = {
      val xdist: Double = x - other.x
      val ydist: Double = y - other.y
      xdist * xdist + ydist * ydist
    }

    def gravityTo(other: FastCircle): (Double, Double) = {
      val distanceSquared: Double = this.distanceTo(other)
      val acceleration = Geometry.gravitation * (other.mass) / ( distanceSquared )
      val factor = acceleration / Math.sqrt(distanceSquared)
      ((other.x - x) * factor, (other.y - y) * factor)
    }

  }

  override def toString: String = "Naive double "+ (if (parallel) " (parallel)" else "")
}
