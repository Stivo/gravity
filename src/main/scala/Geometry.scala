/**
 * Created by Stivo on 17.01.2016.
 */
import Utils._

object Geometry {
  var gravitation = 10
}

case class Point(x: Double, y: Double) {

  def distanceTo(center: Point) = Math.sqrt((x - center.x) ** 2 + (y - center.y) ** 2)

  def -(other: Point): Point = {
    Point(x - other.x, y - other.y)
  }

  def add(acceleration: Acceleration): Point = {
    Point(x + acceleration.x, y + acceleration.y)
  }

  def within(width: Int, height: Int): Boolean = {
    0 <= x && x <= width && 0 <= y && y <= height
  }

  def add(other: Point): Point = Point(x + other.x, y + other.y)

  val length: Double = x ** 2 + (y ** 2)

}

case class Acceleration(x: Double = 0, y: Double = 0) {
  def +(other: Acceleration): Acceleration = Acceleration(x + other.x, y + other.y)
}

object Acceleration {
  def scaled(p: Point, length: Double): Acceleration = {
    val factor: Double = length / p.length
    Acceleration(p.x * factor, p.y * factor)
  }
}