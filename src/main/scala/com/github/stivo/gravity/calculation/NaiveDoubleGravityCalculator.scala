package com.github.stivo.gravity.calculation

import com.github.stivo.gravity.{Geometry, Point, Speed2D, Circle}
import squants.motion.{Velocity, MetersPerSecondSquared, MetersPerSecond}
import squants.time.Time
import scala.collection.GenSeq
import scala.collection.optimizer._

class NaiveDoubleGravityCalculator(parallel: Boolean = true) extends GravityCalculator with FastCircleUser {

  def makeSpeed(point: (Double, Double), timePerTick: Time) = {
    val x: Velocity = MetersPerSecondSquared(point._1) * timePerTick
    val y: Velocity = MetersPerSecondSquared(point._2) * timePerTick
    Speed2D(x, y)
  }

  def addTuples(t1: (Double, Double), t2: (Double, Double)) = (t1._1 + t2._1, t1._2 + t2._2)

  override def calculateForceVectorsForMoreThanOne(circles: IndexedSeq[Circle], timePerTick: Time): IndexedSeq[Speed2D] = {
    val circles1 = if (parallel) circles.par else circles
    val fastCircles = createFastCircles(circles1)

    (for (c1 <- fastCircles)
      yield makeSpeed(fastCircles.filter(c1 ne _).map(c2 => c1.gravityTo(c2)).reduce(addTuples), timePerTick)).toIndexedSeq
  }


  override def toString: String = "Naive double " + (if (parallel) " (parallel)" else "")
}
