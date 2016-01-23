package com.github.stivo.gravity.calculation

import com.github.stivo.gravity.{Speed2D, StopWatch, Circle}
import squants.space.Meters

trait CollisionApplier extends CollisionDetector {

  def applyCollisions(circlesIn: Vector[Circle]): Vector[Circle] = {
    var circles = circlesIn
    var iterations = 1
    var collisions: CollisionGroups = detectCollisions(circles)
    while (!collisions.collisionGroups.isEmpty) {
      var newCircles: Set[Circle] = Set.empty
      var removeCircles: Set[Circle] = Set.empty
      for (collision <- collisions.collisionGroups) {
        val newCircle: Circle = mergeAll(collision.toIndexedSeq)
        newCircles += newCircle
        removeCircles ++= collision
      }
      circles = circles.filterNot(circle => removeCircles.contains(circle)) ++ newCircles
      collisions = detectCollisions(circles)
      iterations += 1
    }
    StopWatch.addEntry("Collision iterations", iterations+"")
    circles
  }

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
