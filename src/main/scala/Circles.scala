import java.awt.Graphics2D
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
          random.nextInt(11) - 5,
          random.nextInt(11) - 5
        )
    }
  }

  def updateCircles(width: Int, height: Int): Vector[Circle] = {
    circles.foreach(_.updatePosition())
    circles = circles.filter(_.withinBounds(width, height))
    circles
  }

  def tick(width: Int, height: Int): Unit = {
    tick += 1
    if (tick % 10 == 0) {
      addCircles(width, height - 50, 10)
    }
    updateCircles(width, height)
  }

}

class Circle(var x: Int, var y: Int, var radius: Double, var xVelocity: Int = 1, var yVelocity: Int = 0) {
  def withinBounds(width: Int, height: Int): Boolean = {
    (0 to width).contains(x) && (0 to height).contains(y)
  }

  def updatePosition(): Unit = {
    x += xVelocity
    y += yVelocity
  }

  def drawTo(g: Graphics2D): Unit = {
    g.fill(new Ellipse2D.Double(x, y, radius, radius))
  }
}