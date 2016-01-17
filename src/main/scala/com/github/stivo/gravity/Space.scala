package com.github.stivo.gravity

import java.awt.{Graphics2D, Color}

import squants.space.Meters

import scala.util.Random

class Space(drawingSurface: DrawingSurface) {

  var circles = Vector[Circle]()
  var tick: Int = 0

  def addCircles(width: Int, height: Int, amount: Int = 1): Unit = {
    val random: Random = new Random()
    circles ++= {
      for (x <- 0 to amount)
        yield new Circle(
          Point(Meters(random.nextDouble() * 20 - 10), Meters(random.nextDouble() * 20 - 10)),
          Meters(random.nextDouble() * 3),
          Acceleration2D()
        )
    }
  }

  addCircles(2000, 1400, 10000)
  //
//    circles +:= new Circle(Point(100, 100), 20, Acceleration())
  //  circles +:= new Circle(Point(500, 500), 5, Acceleration())

//  circles +:= SolarSystem.sun.makeCircle(Point(1000, 500), Color.yellow)
//  circles +:= SolarSystem.earth.makeCircle(Point(1000 - scaleDistance(SolarSystem.sunToEarthDistance), 500), Color.blue)

  println(circles)

  def drawTo(g2d: Graphics2D) = {
    circles.foreach(circle =>
      circle.drawTo(g2d, drawingSurface)
    )
  }


  def crossProduct = {
    for {
      (circle1, index) <- circles.zipWithIndex
      circle2 <- circles.take(index + 1)
      if circle1 ne circle2
    } yield (circle1, circle2)
  }

  def applyCollisions(): Unit = {
//    val colliding = crossProduct.filter { case (c1, c2) => c1.collidesWith(c2) }
//    if (!colliding.isEmpty) {
//      val newCircles = colliding.map {
//        case (c1, c2) =>
//          c1.mergeWith(c2)
//      }
//      val remove = colliding.flatMap {
//        case (c1, c2) => List(c1, c2)
//      }.toSet
//      circles = circles.filterNot(circle => remove.contains(circle))
//      circles ++= newCircles
//    }
  }


  def updateVelocities(getWidth: Int, getHeight: Int) = {
    var forces: Map[Circle, Vector[Acceleration2D]] = Map.empty

//    crossProduct.foreach { case (circle1, circle2) =>
//      val distance = reverseScaleDistance(circle1.center.distanceTo(circle2.center))
//      val gravity = Geometry.gravitation * (circle1.mass * circle2.mass) / (distance ** 2)
//
//      val forces1: Vector[Acceleration2D] = forces.getOrElse(circle1, Vector.empty)
//      val forces2: Vector[Acceleration2D] = forces.getOrElse(circle2, Vector.empty)
//      forces += circle1 -> (forces1 :+ Acceleration2D.scaled((circle2.center - circle1.center), gravity))
//      forces += circle2 -> (forces2 :+ Acceleration2D.scaled((circle1.center - circle2.center), gravity))
//    }
//    forces.foreach {
//      case (circle, accelerations) =>
//        val finalAcceleration: Acceleration2D = accelerations.reduce(_ + _)
//        circle.setGravityPull(finalAcceleration)
//    }
  }

  def updateCircles(): Vector[Circle] = {
    StopWatch.start("Update Position")
    circles.foreach(_.updatePosition())
    circles = circles.filter(_.withinBounds(drawingSurface))
    circles
  }

  def nextTick(): Unit = {
    tick += 1
    if (tick % 10 == 0) {
      //      addCircles(width, height, 1000 - circles.size)
    }
    updateCircles()
  }

}


case class Collision(cir1: Circle, cir2: Circle)