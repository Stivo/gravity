package com.github.stivo.gravity

import java.awt.{Graphics2D, Color}

import squants.mass.Kilograms
import squants.motion._
import squants.space.{Length, Meters}
import squants.time.Seconds

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class Space(drawingSurface: DrawingSurface) {
  val random: Random = new Random()

  var circles = Vector[Circle]()
  var tick: Int = 0

  def randomDouble(range: Double = 1): Double = (random.nextDouble() - 0.5) * range

  def randomPositiveDouble(range: Double = 1): Double = random.nextDouble() * range

  def addCircles(amount: Int = 1): Unit = {
    circles ++= {
      for (x <- 0 to amount)
        yield new Circle(
          Point(Meters(randomDouble(drawingSurface.minimumDrawingArea.toMeters * 3)), Meters(randomDouble(drawingSurface.minimumDrawingArea.toMeters * 3))),
          Meters(randomPositiveDouble(drawingSurface.minimumDrawingArea.toMeters / 200)),
          Speed2D(MetersPerSecond(randomDouble(500000)), MetersPerSecond(randomDouble(500000)))
        )
    }
  }

  addCircles(2000)
  //
  //  circles +:= new Circle(Point(Meters(0), Meters(0)), Meters(0.5), Acceleration2D(), Color.yellow)
  //  circles +:= new Circle(Point(Meters(-10), Meters(0)), Meters(0.2), Acceleration2D(), Color.yellow)
  //  circles +:= new Circle(Point(500, 500), 5, Acceleration())

  circles +:= SolarSystem.sun.makeCircle(Color.yellow)

      circles +:= SolarSystem.earth.makeCircle(Color.blue)
      circles +:= SolarSystem.mercury.makeCircle(Color.green)
      circles +:= SolarSystem.venus.makeCircle(Color.red)
      circles +:= SolarSystem.mars.makeCircle(Color.red)
      circles +:= SolarSystem.pluto.makeCircle(Color.gray)

  //  println(circles)

  def drawTo(g2d: Graphics2D) = {
    circles.foreach(circle =>
      circle.drawTo(g2d, drawingSurface)
    )
  }


  //  def crossProduct = {
  //    for {
  //      (circle1, index) <- circles.zipWithIndex
  //      circle2 <- circles.take(index + 1)
  //      if circle1 ne circle2
  //    } yield (circle1, circle2)
  //  }

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

  def applyCollisions(): Unit = {
    def computeCollidingPairs() = {
      val collidingPairs: ArrayBuffer[(Circle, Circle)] = ArrayBuffer.empty
      crossProduct { case (c1, c2) =>
        if (c1.collidesWith(c2)) {
          collidingPairs.append((c1, c2))
        }
      }
      collidingPairs
    }
    var collidingPairs = computeCollidingPairs()
    while (!collidingPairs.isEmpty) {
      val colliding = collidingPairs
        .groupBy(_._1)
        .map {
        case (key, value) => key +: value.map(_._2)
      }
      val newCircles = colliding.map(circles => mergeAll(circles))
      val remove: Set[Circle] = colliding.flatten.toSet
      circles = circles.filterNot(circle => remove.contains(circle))
      circles ++= newCircles
      collidingPairs = computeCollidingPairs()
    }
  }


  def updateVelocities() = {
    var forces: Map[Circle, (Double, Double)] = Map.empty
    var computations = 0L
    var saved = 0L
    StopWatch.start("Computing forces for each pair")
    crossProduct { case (circle1, circle2) =>
      val distanceSquared: Double = circle1.center.distanceToSquared(circle2.center)
      val mass1: Double = circle1.mass.toKilograms
      val mass2: Double = circle2.mass.toKilograms
      val gravity: Force = Newtons(Geometry.gravitation * (mass1 * mass2) / (distanceSquared))
      val impulse: Momentum = gravity * Main.timeTick
      computations += 2

      val point: Point = circle2.center - circle1.center
      val xLen = point.x.toMeters
      val yLen = point.y.toMeters
      val lengthOfVector = xLen * xLen + yLen * yLen
      val length = Math.sqrt(lengthOfVector)
      val momentum: Momentum = impulse / length

      if (mass2 / mass1 > 0.01) {
        val (vecX, vecY) = forces.getOrElse(circle1, (0.0, 0.0))
        val scalingFactor: Double = (momentum / circle1.mass).toMetersPerSecond

        forces += circle1 -> ((vecX + xLen * scalingFactor, vecY + yLen * scalingFactor))
      } else {
        saved += 1
        //        println(s"Saved $circle1 vs $circle2 update for first because ${circle2.mass / circle1.mass}")
      }
      if (mass1 / mass2 > 0.01) {
        val (vecX, vecY) = forces.getOrElse(circle2, (0.0, 0.0))
        val scalingFactor: Double = (momentum / circle2.mass).toMetersPerSecond

        forces += circle2 -> ((vecX + xLen * scalingFactor * -1, vecY + yLen * scalingFactor * -1))
      } else {
        saved += 1
        //        println(s"Saved $circle1 vs $circle2 update for second because ${circle1.mass / circle2.mass}")
      }
    }
    println(s"Saved $saved of $computations (${saved * 100.0 / computations}%)")
    StopWatch.start("Computing final force")
    forces.foreach {
      case (circle, (vx, vy)) =>
        circle.setGravityPull(new Speed2D(MetersPerSecond(vx), MetersPerSecond(vy)))
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
