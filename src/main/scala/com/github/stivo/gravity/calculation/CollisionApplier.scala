package com.github.stivo.gravity.calculation

import com.github.stivo.gravity.{Speed2D, StopWatch, Circle}
import squants.space.Meters

/**
 * Created by Stivo on 23.01.2016.
 */
trait CollisionApplier {
  def applyCollisions(circles: Vector[Circle]): Vector[Circle]

  def mergeAll(circles: IndexedSeq[Circle]): Circle = {
    val by: IndexedSeq[Circle] = circles.sortBy(-_.radius)
    val newRadius = {
      val allRadsSquared = by.map(_.mass.toKilograms).sum
      Meters(Math.pow(allRadsSquared, 1.0 / 3))
    }
    val speed2D: Speed2D = {
      by.map(cir => cir.acceleration * cir.mass.toKilograms).reduce(_ + _) / by.map(_.mass.toKilograms).sum
    }
    val out = new Circle(
      by(0).center,
      newRadius,
      acceleration = speed2D,
      collisionCount = by.map(_.collisionCount).max + 1,
      color = by(0).color
    )
    out
  }

}
