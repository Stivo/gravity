package com.github.stivo.gravity

import java.awt.{Graphics2D, Color}

import com.github.stivo.gravity.calculation.{StandardGravityCalculator, GravityCalcalutor}
import squants.mass.Kilograms
import squants.motion._
import squants.space.{Length, Meters}
import squants.time.{Days, Hours, Time, Seconds}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class Space(drawingSurface: DrawingSurface,
            var timePerTick: Time = Hours(6),
            var gravityCalculator: GravityCalcalutor = new StandardGravityCalculator()) {

  val random: Random = new Random()

  var time: Time = Days(0)

  var circles = Vector[Circle]()

  private var step: Int = 0

  def randomDouble(range: Double = 1): Double = (random.nextDouble() - 0.5) * range

  def randomPositiveDouble(range: Double = 1): Double = random.nextDouble() * range

  def addCircles(amount: Int = 1, random: Random = random): Unit = {
    circles ++= {
      for (x <- 0 to amount)
        yield new Circle(
          Point(Meters(randomDouble(drawingSurface.minimumDrawingArea.toMeters * 3)), Meters(randomDouble(drawingSurface.minimumDrawingArea.toMeters * 3))),
          Meters(randomPositiveDouble(drawingSurface.minimumDrawingArea.toMeters / 200)),
          Speed2D(MetersPerSecond(randomDouble(500000)), MetersPerSecond(randomDouble(500000)))
        )
    }
  }

  //  addCircles(2000)
  //
  //  circles +:= new Circle(Point(Meters(0), Meters(0)), Meters(0.5), Acceleration2D(), Color.yellow)
  //  circles +:= new Circle(Point(Meters(-10), Meters(0)), Meters(0.2), Acceleration2D(), Color.yellow)
  //  circles +:= new Circle(Point(500, 500), 5, Acceleration())

  //  println(circles)

  def addBodies(body: Iterable[Body]) = {
    body.map(_.makeCircle()).foreach(circles +:= _)
  }

  def circleForBody(body: Body): Option[Circle] = {
    circles.filter(_.body == Some(body)).headOption
  }

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
    val vectors: Iterable[Speed2D] = gravityCalculator.calculateForceVectors(circles, timePerTick)
    StopWatch.start("Computing final force")
    vectors.zip(circles).foreach {
      case (speed, circle) =>
        circle.setGravityPull(speed)
    }
  }

  def updateCircles(): Vector[Circle] = {
    StopWatch.start("Update Position")
    circles.foreach(_.updatePosition(timePerTick))
    circles = circles.filter(_.withinBounds(drawingSurface))
    circles
  }

  def nextTick(): Unit = {
    step += 1
    time += timePerTick
    if (step % 10 == 0) {
      //      addCircles(1000 - circles.size)
    }
    updateVelocities()
    updateCircles()
  }

}
