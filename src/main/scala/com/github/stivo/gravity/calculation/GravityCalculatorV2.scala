package com.github.stivo.gravity.calculation

import com.github.stivo.gravity._
import squants.motion.{Force, MetersPerSecond, Momentum, Newtons}
import squants.time.Time

class GravityCalculatorV2 extends GravityCalculator {

  override def calculateForceVectors(circles: IndexedSeq[Circle], timePerTick: Time): IndexedSeq[Speed2D] = {
    def crossProduct(f: (Circle, Circle) => Unit) = {
      var int1 = 0
      while (int1 < circles.length) {
        var int2 = 0
        while (int2 < int1) {
          f(circles(int1), circles(int2))
          int2 += 1
        }
        int1 += 1
      }
    }

    var forces: Map[Circle, (Double, Double)] = Map.empty
    var computations = 0L
    var saved = 0L
    StopWatch.start("Computing forces for each pair")
    crossProduct { case (circle1, circle2) =>
      val distanceSquared: Double = circle1.center.distanceToSquared(circle2.center)
      val mass1: Double = circle1.mass.toKilograms
      val mass2: Double = circle2.mass.toKilograms
      val gravity: Force = Newtons(Geometry.gravitation * (mass1 * mass2) / (distanceSquared))
      val impulse: Momentum = gravity * timePerTick
      computations += 2

      val point: Point = circle2.center - circle1.center
      val xLen = point.x.toMeters
      val yLen = point.y.toMeters
      val lengthOfVector = xLen * xLen + yLen * yLen
      val length = Math.sqrt(lengthOfVector)
      val momentum: Momentum = impulse / length;
    {
      val (vecX, vecY) = forces.getOrElse(circle1, (0.0, 0.0))
      val scalingFactor: Double = (momentum / circle1.mass).toMetersPerSecond

      forces += circle1 -> ((vecX + xLen * scalingFactor, vecY + yLen * scalingFactor))
    };
    {
      val (vecX, vecY) = forces.getOrElse(circle2, (0.0, 0.0))
      val scalingFactor: Double = (momentum / circle2.mass).toMetersPerSecond

      forces += circle2 -> ((vecX + xLen * scalingFactor * -1, vecY + yLen * scalingFactor * -1))
    }
    }
    circles.map(circle => forces(circle) match {
      case (x, y) => new Speed2D(MetersPerSecond(x), MetersPerSecond(y))
    })
  }
}