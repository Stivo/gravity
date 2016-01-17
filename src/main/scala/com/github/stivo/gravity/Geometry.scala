package com.github.stivo.gravity

/**
 * Created by Stivo on 17.01.2016.
 */

import java.awt.geom.Point2D

import com.github.stivo.gravity.Utils._
import squants.motion.{Acceleration, MetersPerSecond, Velocity}
import squants.space.{Meters, Area, Length}
import squants.time.Seconds

object Geometry {
  var gravitation = 6.674 * (10 ** -11) // N?m2/kg2
}

case class Point(x: Length, y: Length) {

  def distanceToSquared(other: Point): Double = {
    val xDistance= x.toMeters - other.x.toMeters
    val yDistance= y.toMeters - other.y.toMeters
    xDistance * xDistance + yDistance * yDistance
  }

  def -(other: Point): Point = {
    Point(x - other.x, y - other.y)
  }

  def +(other: Point): Point = Point(x + other.x, y + other.y)

  def +(acceleration: Speed2D): Point = {
    Point(x + Main.timeTick * acceleration.x, y + Main.timeTick * acceleration.y)
  }

  def /(double: Double): Point = Point(x / double, y / double)

  def *(double: Double): Point = Point(x * double, y * double)

  def within(drawingSurface: DrawingSurface): Boolean = {
    drawingSurface.isInWidth(x) && drawingSurface.isInHeight(y)
  }

  override def toString = f"${x.toMeters}%.1fm/${y.toMeters}%.1fm"
}

case class Speed2D(x: Velocity = MetersPerSecond(0), y: Velocity = MetersPerSecond(0)) {
  def +(other: Speed2D): Speed2D = Speed2D(x + other.x, y + other.y)

  def *(double: Double): Speed2D = Speed2D(x * double, y * double)

  def /(double: Double): Speed2D = Speed2D(x / double, y / double)

  override def toString = f"Speed: ${x.toMetersPerSecond}%.1fm/s / ${y.toMetersPerSecond}%.1fm/2s"
}

object Speed2D {
  def apply(direction: Point, acceleration: Acceleration) = {
    val resultSpeed = acceleration * Main.timeTick
    val lengthOfVector = direction.x * direction.x + direction.y * direction.y
    val length = Math.sqrt(lengthOfVector.toSquareMeters)
    val scalingFactor: Double = resultSpeed.toMetersPerSecond / length
    new Speed2D(direction.x * scalingFactor / Seconds(1), direction.y * scalingFactor / Seconds(1))
  }
}