import java.awt.{Color, Graphics2D}
import java.awt.geom.Ellipse2D

import scala.util.Random
import Utils._

class Circles {


  var circles = Vector[Circle]()
  var tick: Int = 0

  def addCircles(width: Int, height: Int, amount: Int = 1): Unit = {
    val random: Random = new Random()
    circles ++= {
      for (x <- 0 to amount)
        yield new Circle(
          Point(random.nextInt(width), random.nextInt(height)),
          random.nextDouble() * 10,
          Acceleration()
        )
    }
  }

  addCircles(2000, 1400, 100)
//
//  circles +:= new Circle(Point(100, 100), 20, Acceleration())
//  circles +:= new Circle(Poin  t(500, 500), 20, Acceleration())

  def crossProduct = {
    for {
      (circle1, index) <- circles.zipWithIndex
      circle2 <- circles.take(index + 1)
      if circle1 ne circle2
    } yield (circle1, circle2)
  }

  def checkForCollisions() = {
    crossProduct
      .filter{ case (c1, c2) => c1.collidesWith(c2)}
      .map(Collision.tupled)
  }


  def updateVelocities(getWidth: Int, getHeight: Int) = {
    var forces: Map[Circle, Vector[Acceleration]] = Map.empty

    crossProduct.foreach { case (circle1, circle2) =>
      val distance = circle1.center.distanceTo(circle2.center)
      val gravity = Geometry.gravitation * (circle1.mass * circle2.mass) / (distance ** 2)

      val forces1: Vector[Acceleration] = forces.getOrElse(circle1, Vector.empty)
      val forces2: Vector[Acceleration] = forces.getOrElse(circle2, Vector.empty)
      forces += circle1 -> (forces1 :+ Acceleration.scaled((circle2.center - circle1.center), gravity))
      forces += circle2 -> (forces2 :+ Acceleration.scaled((circle1.center - circle2.center), gravity))
    }
    forces.foreach {
      case (circle, accelerations) =>
        val finalAcceleration: Acceleration = accelerations.reduce(_ + _)
        circle.setGravityPull(finalAcceleration)
    }
  }

  def updateCircles(width: Int, height: Int): Vector[Circle] = {
    StopWatch.start("Update Position")
    circles.foreach(_.updatePosition())
    circles = circles.filter(_.withinBounds(width, height))
    circles
  }

  def tick(width: Int, height: Int): Unit = {
    tick += 1
    if (tick % 10 == 0) {
//      addCircles(width, height, 1000 - circles.size)
    }
    updateCircles(width, height)
  }

}


case class Collision(cir1: Circle, cir2: Circle)