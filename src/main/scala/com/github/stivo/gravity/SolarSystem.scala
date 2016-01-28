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

  val sun = new Body(Kilograms(1.988435 * (10 ** 30)), Kilometers(695500), MetersPerSecond(0), Color.yellow, "sun")
  val mercury = new Body(Kilograms(3.3011 * (10 ** 23)), Kilometers(2439.7), MetersPerSecond(59000), Color.gray, "mercury")
  val venus = new Body(Kilograms(4.8675 * (10 ** 24)), Kilometers(12100), MetersPerSecond(35260), Color.orange, "venus")
  val earth = new Body(Kilograms(5.9726 * (10 ** 24)), Kilometers(6371), MetersPerSecond(30290), Color.blue, "earth")
  val moon = new Body(Kilograms(0.07342 * (10 ** 24)), Kilometers(1737), MetersPerSecond(1076) + earth.speed, Color.gray, "moon")
  val mars = new Body(Kilograms(6.4171 * (10 ** 23)), Kilometers(6800), MetersPerSecond(24077), Color.red, "mars")
  val jupiter = new Body(Kilograms(1898.3 * (10 ** 24)), Kilometers(71492), MetersPerSecond(13720), new Color(165, 42, 42), "jupiter")
  val saturn = new Body(Kilograms(568.36 * (10 ** 24)), Kilometers(60268), MetersPerSecond(9090), new Color(217, 201, 102), "saturn")
  val uranus = new Body(Kilograms(86.8 * (10 ** 24)), Kilometers(51118), MetersPerSecond(6490), new Color(74, 232, 211), "uranus")
  val neptune = new Body(Kilograms(102 * (10 ** 24)), Kilometers(49525), MetersPerSecond(5370), new Color(47, 74, 168), "neptune")
  val pluto = new Body(Kilograms(0.0146 * (10 ** 24)), Kilometers(2370), MetersPerSecond(6100), Color.darkGray, "pluto")

  val bodies = List(sun, mercury, earth, moon, venus, mars, jupiter, saturn, uranus, neptune, pluto)

  def distanceToSun(body: Body): Length = body match {
    case `sun` => Kilometers(0)
    case `mercury` => Kilometers(46 * (10 ** 6))
    case `earth` => sunToEarthDistance
    case `moon` => Kilometers(0.378 * (10 ** 6)) + sunToEarthDistance
    case `venus` => Kilometers(107.477 * (10 ** 6))
    case `mars` => Kilometers(227.94 * (10 ** 6))
    case `jupiter` => Kilometers(740.52 * (10 ** 6))
    case `saturn` => Kilometers(1514.5 * (10 ** 6))
    case `uranus` => Kilometers(3003.6 * (10 ** 6))
    case `neptune` => Kilometers(4545.7 * (10 ** 6))
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

class Body(val mass: Mass, val radius: Length, val speed: Velocity, val color: Color = Color.black, val name: String = "") {

  def makeCircle() = new Circle(
    Point(SolarSystem.distanceToSun(this) * -1, Meters(0)),
    radius,
    color = color,
    speed = Speed2D(MetersPerSecond(0), speed),
    massIn = Some(mass),
    body = Some(this)
  )
}
