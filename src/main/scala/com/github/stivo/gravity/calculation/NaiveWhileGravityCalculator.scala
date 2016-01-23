package com.github.stivo.gravity.calculation


import com.github.stivo.gravity.Circle
import com.github.stivo.gravity.Point
import com.github.stivo.gravity.Speed2D
import com.github.stivo.gravity.{Point, Speed2D, Circle}
import squants.motion.{Velocity, MetersPerSecondSquared, MetersPerSecond}
import squants.time.Time

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class NaiveWhileGravityCalculator extends GravityCalculator {

  override def calculateForceVectorsForMoreThanOne(circles: IndexedSeq[Circle], timePerTick: Time): IndexedSeq[Speed2D] = {
    val seconds: Double = timePerTick.toSeconds
    val ds: ArrayBuffer[Speed2D] = new ArrayBuffer[Speed2D](circles.size)
    var counter1 = 0
    while (counter1 < circles.size) {
      var counter2 = 0
      val circle1 = circles(counter1)
      var x = 0.0
      var y = 0.0
      while (counter2 < circles.size) {
        if (counter1 != counter2) {
          val circle2 = circles(counter2)
          val to: Point = circle1.gravityTo(circle2)
          x += to.x.toMeters
          y += to.y.toMeters
        }
        counter2 += 1
      }
      ds.append(new Speed2D(MetersPerSecond(x * seconds), MetersPerSecond(y * seconds)))
      counter1 += 1
    }
    ds
  }

  override def toString: String = "Naive with while"

}
