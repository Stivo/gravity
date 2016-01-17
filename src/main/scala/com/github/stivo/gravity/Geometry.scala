package com.github.stivo.gravity

/**
 * Created by Stivo on 17.01.2016.
 */

import java.awt.geom.Point2D

import com.github.stivo.gravity.Utils._
import squants.motion.{MetersPerSecond, Velocity}
import squants.space.{Meters, Area, Length}
import squants.time.Seconds

object Geometry {
  var gravitation = 6.674 * (10 ** -11) // N?m2/kg2
}

case class Point(x: Length, y: Length) {

  def distanceTo(center: Point) = {
    val area: Area = (x - center.x) ** 2 + (y - center.y) ** 2
    Meters(Math.sqrt(area.toSquareMeters))
  }

  def -(other: Point): Point = {
    Point(x - other.x, y - other.y)
  }

  def +(other: Point): Point = Point(x + other.x, y + other.y)

  def +(acceleration: Acceleration2D): Point = {
    Point(x + acceleration.x * Seconds(1), y + acceleration.y * Seconds(1))
  }

  def /(double: Double): Point = Point(x / double, y / double)

  def *(double: Double): Point = Point(x * double, y * double)

  def within(drawingSurface: DrawingSurface): Boolean = {
    drawingSurface.isInWidth(x) && drawingSurface.isInHeight(y)
  }

//  val length: Double = x ** 2 + (y ** 2)

//  def asPoint2D = new Point2D.Double(x, y)

}

case class Acceleration2D(x: Velocity = MetersPerSecond(0), y: Velocity = MetersPerSecond(0)) {
  def +(other: Acceleration2D): Acceleration2D = Acceleration2D(x + other.x, y + other.y)

  def *(double: Double): Acceleration2D = Acceleration2D(x * double, y * double)

  def /(double: Double): Acceleration2D = Acceleration2D(x / double, y / double)
}

object Acceleration2D {
//  def scaled(p: Point, length: Double): Acceleration2D = {
//    val factor: Double = length / p.length
//    Acceleration2D(p.x * factor, p.y * factor)
//  }
}