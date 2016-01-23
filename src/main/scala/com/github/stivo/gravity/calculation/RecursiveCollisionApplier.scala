package com.github.stivo.gravity.calculation

import java.awt.Color

import com.github.stivo.gravity.Circle

import scala.collection.generic.SeqFactory

class RecursiveCollisionApplier extends CollisionApplier with FastCircleUser {
  type T = FastCircle
  type Coll[T] = Vector[T]

  def empty: Coll[T] = Vector.empty

  var currentCircles: Map[Int, FastCircle] = null


  class Quadrant(
                  val xLowYLow: Coll[T],
                  val xLowYHigh: Coll[T],
                  val xHighYLow: Coll[T],
                  val xHighYHigh: Coll[T]
                  ) {
    def detectCollisions(level: Int = 0, collisionGroups: CollisionGroups = new CollisionGroups): CollisionGroups = {
      val ts = List(xLowYLow, xLowYHigh, xHighYLow, xHighYHigh)
      for {
         quadrant <- if (level == 0) ts.par else ts
      } {
        detectCollisions(quadrant, collisionGroups, level)
      }
      collisionGroups
    }

    def computeCollisions(collection: Coll[T], collisionGroups: CollisionGroups): Unit = {
      for {
        (c1, i) <- collection.zipWithIndex.par
        c2 <- collection.drop(i)
        if c1 ne c2
        if c1.collidesWith(c2)
      } {
        collisionGroups.addCollision(c1.circle, c2.circle)
      }
    }

    def detectCollisions(collection: Coll[T], collisionGroups: CollisionGroups, level: Int): Unit = {
      if (collection.size < 200) {
        computeCollisions(collection, collisionGroups)
      } else {
        val lowestX = collection.view.map(_.xLow).min
        val lowestY = collection.view.map(_.yLow).min
        val highestX = collection.view.map(_.xHigh).max
        val highestY = collection.view.map(_.yHigh).max
        Quadrant(collection,
          (lowestX + highestX) / 2,
          (lowestY + highestY) / 2,
          lowestX, highestX, lowestY, highestY).detectCollisions(level + 1, collisionGroups)
      }
    }

  }

  object Quadrant {
    def apply(circles: Vector[FastCircle],
              xLine: Double = 0,
              yLine: Double = 0,
              xLow: Double = Double.NegativeInfinity,
              xHigh: Double = Double.PositiveInfinity,
              yLow: Double = Double.NegativeInfinity,
              yHigh: Double = Double.PositiveInfinity): Quadrant = {
      var xLowYLow: Coll[T] = empty
      var xLowYHigh: Coll[T] = empty
      var xHighYLow: Coll[T] = empty
      var xHighYHigh: Coll[T] = empty

      circles
        .withFilter(circle => circle.xLow >= xLow && circle.xHigh <= xHigh)
        .withFilter(circle => circle.yLow >= yLow && circle.yHigh <= yHigh)
        .foreach { circle =>
        if (circle.xHigh >= xLine && circle.xLow <= xHigh) {
          if (circle.yHigh >= yLine && circle.yLow <= yHigh) {
            xHighYHigh +:= circle
          }
          if (circle.yLow <= yLine && circle.yHigh >= yLow) {
            xHighYLow +:= circle
          }
        }
        if (circle.xLow <= xLine && circle.xHigh >= xLow) {
          if (circle.yHigh >= yLine && circle.yLow <= yHigh) {
            xLowYHigh +:= circle
          }
          if (circle.yLow <= yLine && circle.yHigh >= yLow) {
            xLowYLow +:= circle
          }
        }
      }
      new Quadrant(xLowYLow, xLowYHigh, xHighYLow, xHighYHigh)
    }
  }


  override def detectCollisions(circles: Iterable[Circle]): CollisionGroups = {
    val fastCircles = createFastCircles(circles.toSeq)
    Quadrant(fastCircles.toVector).detectCollisions()
  }
  
 }



