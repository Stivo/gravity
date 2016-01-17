import java.awt.{Color, Graphics2D}
import java.awt.geom.Ellipse2D

import scala.util.Random

/**
 * Created by Stivo on 17.01.2016.
 */
class Circles {


  var circles = Vector[Circle]()
  var tick: Int = 0

  def addCircles(width: Int, height: Int, amount: Int = 1): Unit = {
    val random: Random = new Random()
    circles ++= {
      for (x <- 0 to amount)
        yield new Circle(
          random.nextInt(width),
          random.nextInt(height),
          random.nextDouble() * 10,
          (random.nextDouble() - 0.5) * 5,
          (random.nextDouble() - 0.5) * 5
        )
    }
  }

  addCircles(2000, 1400, 1000)

  def checkForCollisions() = {
    for {
      (circle1, index) <- circles.zipWithIndex
      circle2 <- circles.take(index + 1)
      if circle1 ne circle2
      if circle1.collidesWith(circle2)
    } yield Collision(circle1, circle2)
  }

  def updateCircles(width: Int, height: Int): Vector[Circle] = {
    StopWatch.start("Update Position")
    circles.foreach(_.updatePosition())
    StopWatch.start("Check bounds")
    circles = circles.filter(_.withinBounds(width, height))
    StopWatch.start("After check bounds")
    circles
  }

  def tick(width: Int, height: Int): Unit = {
    tick += 1
    if (tick % 10 == 0) {
      addCircles(width, height, 1000 - circles.size)
    }
    updateCircles(width, height)
  }

}

class Circle(var x: Double,
             var y: Double,
             var radius: Double,
             var xVelocity: Double = 1,
             var yVelocity: Double = 0,
             var color: Color = Color.black) {

  def xInt = Math.round(x)
  def yInt = Math.round(y)

  def withinBounds(width: Int, height: Int): Boolean = {
    (0 to width).contains(xInt) && (0 to height).contains(yInt)
  }

  def collidesWith(circle2: Circle) = {
    if (circle2.asEllipsis.intersects(x, y, radius, radius)) {
      color = Color.red
      circle2.color = Color.red
      true
    } else {
      false
    }
  }

  def updatePosition(): Unit = {
    x += xVelocity
    y += yVelocity
  }

  def drawTo(g: Graphics2D): Unit = {
    g.setPaint(color)
    g.fill(asEllipsis)
  }

  def asEllipsis: Ellipse2D.Double = {
    new Ellipse2D.Double(x, y, radius, radius)
  }

  override def toString: String =
    s"$x/$y ($radius) $xVelocity/$yVelocity"

}

case class Collision(cir1: Circle, cir2: Circle)