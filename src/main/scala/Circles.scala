import java.awt.Graphics2D
import java.awt.geom.Ellipse2D

/**
 * Created by Stivo on 17.01.2016.
 */
class Circles {

  var circles = Vector[Circle]()
  var tick: Int = 0

  def addCirclesAtX(width: Int, height: Int, x: Int = 0): Unit = {
    circles ++= {
      for (y <- 0 to height by 5)
        yield new Circle(x, y, 4)
    }
  }

  for (x <- 0 to 500 by 10) {
    addCirclesAtX(2000, 1100, x)
  }

  def moveCirclesRight(width: Int): Vector[Circle] =
    circles
      .withFilter(circle => circle.x < width + 100)
      .map(circle => circle.copy(x = circle.x + 1))

  def tick(width: Int, height: Int): Unit = {
    tick += 1
    if (tick % 10 == 0) {
      addCirclesAtX(width, height - 50)
    }
    circles = moveCirclesRight(width)
  }

}

case class Circle(x: Int, y: Int, radius: Double) {
  def drawTo(g: Graphics2D): Unit = {
    g.fill(new Ellipse2D.Double(x, y, radius, radius))
  }
}