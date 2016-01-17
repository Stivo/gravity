package com.github.stivo.gravity

/**
 * Created by Stivo on 17.01.2016.
 */

import java.awt.Color

import com.github.stivo.gravity.Utils._
import squants.mass.{Kilograms, Mass}
import squants.motion.{MetersPerSecondSquared, Acceleration, Velocity, MetersPerSecond}
import squants.space.{Length, Kilometers}

object SolarSystem {

  val sun = Body(Kilograms(1.988435 * (10 ** 30)), Kilometers(695500), MetersPerSecond(0))
  val earth = Body(Kilograms(5.9721986 * (10 ** 24)), Kilometers(6371), MetersPerSecond(30000))

  val sunToEarthDistance = Kilometers(1.472 * (10 ** 8))

  def main(args: Array[String]): Unit = {
    println(sun.gravity(earth, sunToEarthDistance))
  }

//  def scaleDistance(distance: Double) = 500 * distance / sunToEarthDistance
//
//  def reverseScaleDistance(distance: Double) = distance * sunToEarthDistance / 500
//
//  def scaleRadius(radiusInKm: Double) = {
//    Math.min(500, Math.max(1.5, 10000 * radiusInKm / sunToEarthDistance))
//  }
//
//  def scaleAcceleration(acceleration: Acceleration) = {
//    acceleration / 1000000
//  }
}

case class Body(mass: Mass, radius: Length, speedInKmPerSecond: Velocity) {
  /**
   * @return m/ s /s
    */
  def gravity(other: Body, distance: Length): Acceleration = {
    MetersPerSecondSquared(Geometry.gravitation * mass.toKilograms * other.mass.toKilograms / (distance.toMeters ** 2))
  }

  def makeCircle(p: Point, color: Color = Color.black) = new Circle(
    p,
    radius,
    color = color,
    acceleration = Acceleration2D(MetersPerSecond(0), speedInKmPerSecond),
    massIn = Some(mass)
  )
}
