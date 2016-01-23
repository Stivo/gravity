package com.github.stivo.gravity.calculation

import com.github.stivo.gravity.Circle

import scala.collection.GenSeq

class StandardCollisionApplierWithoutUnits extends CollisionApplier with FastCircleUser {

   override def detectCollisions(circles: Iterable[Circle]): CollisionGroups = {
     val fastCircles: GenSeq[FastCircle] = createFastCircles(circles.toVector)
     val groups: CollisionGroups = new CollisionGroups
     for {
       (c1, i) <- fastCircles.zipWithIndex.par
       c2 <- fastCircles.drop(i)
       if c1 ne c2
       if c1.collidesWith(c2)
     } {
       groups.addCollision(c1.circle, c2.circle)
     }
     groups
   }
 }
