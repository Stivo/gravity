package com.github.stivo.gravity.calculation

import com.github.stivo.gravity.{StopWatch, Circle}

class StandardCollisionApplier extends CollisionApplier {

  override def detectCollisions(circles: Iterable[Circle]): CollisionGroups = {
    val groups: CollisionGroups = new CollisionGroups
    for {
      (c1, i) <- circles.zipWithIndex
      c2 <- circles.drop(i)
      if c1 ne c2
      if c1.collidesWith(c2)
    } {
      groups.addCollision(c1, c2)
    }
    groups
  }
}
