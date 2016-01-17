package com.github.stivo.gravity

import java.awt.{Graphics2D, Color}

import squants.mass.Kilograms
import squants.motion.{Force, Newtons, MetersPerSecondSquared, MetersPerSecond}
import squants.space.Meters

import scala.util.Random

class Space(drawingSurface: DrawingSurface) {
  val random: Random = new Random()

  var circles = Vector[Circle]()
  var tick: Int = 0

  def randomDouble(range: Double = 1): Double = (random.nextDouble() - 0.5) * range

  def addCircles(amount: Int = 1): Unit = {
    circles ++= {
      for (x <- 0 to amount)
        yield new Circle(
          Point(Meters(randomDouble(drawingSurface.minimumDrawingArea.toMeters * 3)), Meters(randomDouble(drawingSurface.minimumDrawingArea.toMeters * 3))),
          Meters(randomDouble(drawingSurface.minimumDrawingArea.toMeters / 50)),
          Speed2D(MetersPerSecond(randomDouble(10000)), MetersPerSecond(randomDouble(10000)))
        )
    }
  }

  addCircles(1000)
  //
  //  circles +:= new Circle(Point(Meters(0), Meters(0)), Meters(0.5), Acceleration2D(), Color.yellow)
  //  circles +:= new Circle(Point(Meters(-10), Meters(0)), Meters(0.2), Acceleration2D(), Color.yellow)
  //  circles +:= new Circle(Point(500, 500), 5, Acceleration())

//  circles +:= SolarSystem.sun.makeCircle(Color.yellow)
//  circles +:= SolarSystem.earth.makeCircle(Color.blue)
//  circles +:= SolarSystem.mercury.makeCircle(Color.green)
//  circles +:= SolarSystem.venus.makeCircle(Color.red)
//  circles +:= SolarSystem.mars.makeCircle(Color.red)
//  circles +:= SolarSystem.pluto.makeCircle(Color.gray)

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

  def mergeAll(circles: Vector[Circle]): Circle = {
    val by: Vector[Circle] = circles.sortBy(-_.radius)
    val newRadius = {
      val allRadsSquared = by.map(_.radius.toMeters).map(rad => rad * rad).sum
      Meters(Math.sqrt(allRadsSquared))
    }
    val speed2D: Speed2D = {
      by.map(cir => cir.acceleration * cir.mass.toKilograms).reduce(_ + _) / by.map(_.mass.toKilograms).sum
    }
    new Circle(
      by(0).center,
      newRadius,
      acceleration = speed2D,
      collisionCount = by.map(_.collisionCount).max + 1,
      color = by(0).color
    )
  }

  def applyCollisions(): Unit = {
    var collidingPairs = crossProduct
      .filter { case (c1, c2) => c1.collidesWith(c2) }
    while (!collidingPairs.isEmpty) {
      val colliding: Iterable[Vector[Circle]] = collidingPairs
        .groupBy(_._1)
        .map {
        case (key, value) => key +: value.map(_._2)
      }
      val newCircles = colliding.map(circles => mergeAll(circles))
      val remove: Set[Circle] = colliding.flatten.toSet
      circles = circles.filterNot(circle => remove.contains(circle))
      circles ++= newCircles
      collidingPairs = crossProduct
        .filter { case (c1, c2) => c1.collidesWith(c2) }
    }
  }


  def updateVelocities() = {
    var forces: Map[Circle, Vector[Speed2D]] = Map.empty

    crossProduct.foreach { case (circle1, circle2) =>
      val distanceSquared: Double = circle1.center.distanceToSquared(circle2.center)
      val gravity: Force = Newtons(Geometry.gravitation * (circle1.mass.toKilograms * circle2.mass.toKilograms) / (distanceSquared))

      val forces1: Vector[Speed2D] = forces.getOrElse(circle1, Vector.empty)
      val forces2: Vector[Speed2D] = forces.getOrElse(circle2, Vector.empty)

      forces += circle1 -> (forces1 :+ Speed2D((circle2.center - circle1.center), gravity / circle1.mass))
      forces += circle2 -> (forces2 :+ Speed2D((circle1.center - circle2.center), gravity / circle2.mass))
    }
    forces.foreach {
      case (circle, accelerations) =>
        val finalAcceleration: Speed2D = accelerations.reduce(_ + _)
        circle.setGravityPull(finalAcceleration)
    }
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
      //      addCircles(1000 - circles.size)
    }
    updateCircles()
  }

}


case class Collision(cir1: Circle, cir2: Circle)