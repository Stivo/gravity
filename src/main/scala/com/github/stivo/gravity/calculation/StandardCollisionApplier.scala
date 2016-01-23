package com.github.stivo.gravity.calculation

import com.github.stivo.gravity.{StopWatch, Circle}

class StandardCollisionApplier extends CollisionApplier {
  override def applyCollisions(circlesIn: Vector[Circle]) = {
    var circles = circlesIn
    var collisions = 0
    def applyNextCollision(): Boolean = {
      var collisionCandidates = circles.toSet
      while (!collisionCandidates.isEmpty) {
        val candidate = collisionCandidates.head
        collisionCandidates = collisionCandidates.tail
        val collidingWithCandidate = collisionCandidates.filter(_.collidesWith(candidate))
        if (!collidingWithCandidate.isEmpty) {
          val colliding = collidingWithCandidate + candidate
          val newCircle = mergeAll(colliding.toIndexedSeq)
          circles = circles.filterNot(colliding.contains(_))
          circles = circles :+ newCircle
          return true
        }
        collisionCandidates -= candidate
      }
      false
    }
    while (applyNextCollision()) {
      collisions += 1
    }
    StopWatch.addEntry("Collisions", "" + collisions)
    circles
  }
}
