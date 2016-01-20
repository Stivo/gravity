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

  val sun = new Body(Kilograms(1.988435 * (10 ** 30)), Kilometers(695500), MetersPerSecond(0), Color.yellow)
  val mercury = new Body(Kilograms(3.3011 * (10 ** 23)), Kilometers(2439.7), MetersPerSecond(59000), Color.gray)
  val earth = new Body(Kilograms(5.9726 * (10 ** 24)), Kilometers(6371), MetersPerSecond(30290), Color.blue)
  val venus = new Body(Kilograms(4.8675 * (10 ** 24)), Kilometers(12100), MetersPerSecond(35260), Color.orange)
  val mars = new Body(Kilograms(6.4171 * (10 ** 23)), Kilometers(6800), MetersPerSecond(24077), Color.red)
  val pluto = new Body(Kilograms(0.01303 * (10 ** 24)), Kilometers(1187), MetersPerSecond(4670), Color.darkGray)

  val bodies = List(sun, mercury, earth, venus, mars, pluto)

  def distanceToSun(body: Body): Length = body match {
    case `sun` => Kilometers(0)
    case `mercury` => Kilometers(46 * (10 ** 6))
    case `earth` => sunToEarthDistance
    case `venus` => Kilometers(107.477 * (10 ** 6))
    case `mars` => Kilometers(227.94 * (10 ** 6))
    case `pluto` => Kilometers(4436.8 * (10 ** 6))
  }

  val sunToEarthDistance = Kilometers(147.09 * (10 ** 6))

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

class Body(mass: Mass, radius: Length, speedInKmPerSecond: Velocity, color: Color = Color.black) {

  def makeCircle() = new Circle(
    Point(SolarSystem.distanceToSun(this) * -1, Meters(0)),
    radius,
    color = color,
    acceleration = Speed2D(MetersPerSecond(0), speedInKmPerSecond),
    massIn = Some(mass),
    body = Some(this)
  )
}
