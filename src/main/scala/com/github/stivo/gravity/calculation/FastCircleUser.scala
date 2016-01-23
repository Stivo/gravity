package com.github.stivo.gravity.calculation

import com.github.stivo.gravity.{Circle, Geometry}

import scala.collection.GenSeq

trait FastCircleUser {

  class FastCircle(val circle: Circle) {

    val x = circle.center.x.toMeters
    val y = circle.center.y.toMeters
    val radius: Double = circle.radius.toMeters
    lazy val xLow = x - radius
    lazy val xHigh = x + radius
    lazy val yLow = y - radius
    lazy val yHigh = y + radius
    val mass = circle.mass.toKilograms
    val id = circle.id

    def distanceTo(other: FastCircle): Double = {
      val xdist: Double = x - other.x
      val ydist: Double = y - other.y
      xdist * xdist + ydist * ydist
    }

    def collidesWith(other: FastCircle): Boolean = {
      val d: Double = this.radius + other.radius
      distanceTo(other) < d * d
    }

    def gravityTo(other: FastCircle): (Double, Double) = {
      val distanceSquared: Double = this.distanceTo(other)
      val acceleration = Geometry.gravitation * (other.mass) / (distanceSquared)
      val factor = acceleration / Math.sqrt(distanceSquared)
      ((other.x - x) * factor, (other.y - y) * factor)
    }

    override def equals(obj: scala.Any): Boolean = {
      obj match {
        case other: FastCircle => circle == other.circle
        case _ => false
      }
    }

    override def hashCode(): Int = circle.hashCode()
  }

  def createFastCircles(circles1: GenSeq[Circle]): GenSeq[FastCircle] = {
    circles1.map(new FastCircle(_))
  }

}
