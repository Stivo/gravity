package com.github.stivo.gravity

/**
 * Created by Stivo on 17.01.2016.
 */

import java.awt.Color

import com.github.stivo.gravity.Utils._
import squants.mass.{Kilograms, Mass}
import squants.motion._
import squants.space.{Meters, Length, Kilometers}

object SolarSystem {

  val sun = Body(Kilograms(1.988435 * (10 ** 30)), Kilometers(695500), MetersPerSecond(0))
  val mercury = Body(Kilograms(3.3011 * (10 ** 23)), Kilometers(2439.7), MetersPerSecond(59000))
  val earth = Body(Kilograms(5.9721986 * (10 ** 24)), Kilometers(6371), MetersPerSecond(30000))
  val venus = Body(Kilograms(5.9721986 * (10 ** 24)), Kilometers(12100), MetersPerSecond(35000))
  val mars = Body(Kilograms(6.4171 * (10 ** 23)), Kilometers(6800), MetersPerSecond(24077))
  val pluto = Body(Kilograms(0.01303 * (10 ** 24)), Kilometers(1187), MetersPerSecond(4670))

  def distanceToSun(body: Body): Length = body match {
    case `sun` => Kilometers(0)
    case `mercury` => Kilometers(46 * (10 ** 6))
    case `earth` => sunToEarthDistance
    case `venus` => Kilometers(108200000)
    case `mars` => Kilometers(227940000)
    case `pluto` => Kilometers(4436.8 * (10 ** 6))
  }

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
   * @return kg * m/ s**2
   */
  def gravity(other: Body, distance: Length): Force = {
    Newtons(Geometry.gravitation * mass.toKilograms * other.mass.toKilograms / (distance.toMeters ** 2))
  }

  def makeCircle(color: Color = Color.black) = new Circle(
    Point(SolarSystem.distanceToSun(this) * -1, Meters(0)),
    radius,
    color = color,
    acceleration = Speed2D(MetersPerSecond(0), speedInKmPerSecond),
    massIn = Some(mass)
  )
}
