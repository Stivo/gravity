/**
 * Created by Stivo on 17.01.2016.
 */

import java.awt.geom.Point2D

import Utils._

object Geometry {
  var gravitation = 100
}

case class Point(x: Double, y: Double) {

  def distanceTo(center: Point) = Math.sqrt((x - center.x) ** 2 + (y - center.y) ** 2)

  def -(other: Point): Point = {
    Point(x - other.x, y - other.y)
  }
  def +(other: Point): Point = Point(x + other.x, y + other.y)

  def +(acceleration: Acceleration): Point = {
    Point(x + acceleration.x, y + acceleration.y)
  }

  def /(double: Double): Point = Point(x / double, y / double)

  def *(double: Double): Point = Point(x * double, y * double)

  def within(width: Int, height: Int): Boolean = {
    0 <= x && x <= width && 0 <= y && y <= height
  }

  val length: Double = x ** 2 + (y ** 2)

  def asPoint2D = new Point2D.Double(x, y)

}

case class Acceleration(x: Double = 0, y: Double = 0) {
  def +(other: Acceleration): Acceleration = Acceleration(x + other.x, y + other.y)
  def *(double: Double): Acceleration = Acceleration(x * double, y * double)
  def /(double: Double): Acceleration = Acceleration(x / double, y / double)
}

object Acceleration {
  def scaled(p: Point, length: Double): Acceleration = {
    val factor: Double = length / p.length
    Acceleration(p.x * factor, p.y * factor)
  }
}