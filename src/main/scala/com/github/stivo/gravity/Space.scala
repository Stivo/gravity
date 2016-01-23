package com.github.stivo.gravity

import java.awt.{Graphics2D, Color}

import com.github.stivo.gravity.calculation._
import squants.mass.Kilograms
import squants.motion._
import squants.space.{Length, Meters}
import squants.time.{Days, Hours, Time, Seconds}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class Space(drawingSurface: DrawingSurface,
            var timePerTick: Time = Hours(6),
            val gravityCalculator: GravityCalculator = new NaiveDoubleGravityCalculator(),
            val collisionApplier: CollisionApplier = new StandardCollisionApplier(),
            val random: Random = new Random()) {


  var time: Time = Days(0)

  val circleLock = new Object
  var circles = Vector[Circle]()

  private var step: Int = 0

  def randomDouble(range: Double = 1): Double = (random.nextDouble() - 0.5) * range

  def randomPositiveDouble(range: Double = 1): Double = random.nextDouble() * range

  def randomLightColor(): Color = {
    def randomLight = random.nextInt(199) + 56
    new Color(randomLight, randomLight, randomLight)
  }

  def addCircles(amount: Int = 1, percentOfScreen: Double = 0.01): Unit = {
    circleLock.synchronized {
      circles ++= {
        def randomDoubleInSpace = randomDouble(drawingSurface.minimumDrawingArea.toMeters * 3)
        for (x <- 1 to amount)
          yield new Circle(
            Point(Meters(randomDoubleInSpace) + drawingSurface.xOffset, Meters(randomDoubleInSpace) + drawingSurface.yOffset),
            Meters(randomPositiveDouble(drawingSurface.minimumDrawingArea.toMeters * percentOfScreen)),
            Speed2D(MetersPerSecond(randomDouble(100000)), MetersPerSecond(randomDouble(100000))),
            color = randomLightColor()
          )
      }
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
    drawingSurface.setGraphics(g2d)
    circles.foreach(circle =>
      circle.drawTo(g2d, drawingSurface)
    )
    drawingSurface.drawScale()
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


  def applyCollisions(): Unit = {
    circles = collisionApplier.applyCollisions(circles)
  }

  def updateVelocities() = {
    val vectors: Iterable[Speed2D] = gravityCalculator.calculateForceVectors(circles, timePerTick)
    vectors.zip(circles).foreach {
      case (speed, circle) =>
        circle.setGravityPull(speed)
    }
  }

  def updateCircles(): Vector[Circle] = {
    circles.foreach(_.updatePosition(timePerTick))
    circles = circles.filter(_.withinBounds(drawingSurface))
    circles
  }

  def nextTick(): Unit = {
    step += 1
    time += timePerTick
    circles.synchronized {
      StopWatch.reset()
      StopWatch.start("Updating velocities")
      updateVelocities()
      StopWatch.start("Updating positions")
      updateCircles()
      StopWatch.start("Applying collisions")
      applyCollisions()
      StopWatch.finish()
    }
  }

}
