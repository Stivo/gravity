package com.github.stivo.gravity

import java.awt.geom.Ellipse2D
import java.awt.{Color, Graphics2D}

import com.github.stivo.gravity.Utils._
import squants.mass.{Kilograms, Mass}
import squants.space.{Area, Length}


object Circle {
  private var id = 0
}

class Circle(var center: Point,
             var radius: Length,
             var acceleration: Acceleration2D = Acceleration2D(),
             var color: Color = Color.black,
             var collisionCount: Int = 0,
             var massIn: Option[Mass] = None) {

  val id = {
    Circle.id += 1
    Circle.id
  }

  var gravityPull: Acceleration2D = Acceleration2D()

  def withinBounds(drawingSurface: DrawingSurface): Boolean = {
    center.within(drawingSurface)
  }

  def collidesWith(circle2: Circle) = {
    center.distanceTo(circle2.center) < radius + circle2.radius
  }

//  def mergeWith(other: Circle): Circle = {
//    val newMass = mass + other.mass
//    val newRadius = Math.sqrt(newMass)
//    val newAcceleration = (acceleration * mass + other.acceleration * mass) / newMass
//    val newCenter = this.center + (this.center - other.center) * (other.mass / newMass)
//    new Circle(
//      center,
//      newRadius,
//      newAcceleration,
//      collisionCount = Math.max(collisionCount, other.collisionCount) + 1
//    )
//  }

  def updatePosition(): Unit = {
    acceleration = acceleration + gravityPull / mass
    center = center.+(acceleration)
  }

  def drawTo(g: Graphics2D, drawingSurface: DrawingSurface): Unit = {
    g.setPaint(color)
    g.fill(asEllipsis(drawingSurface))
    //    if (this.collisionCount > 0) {
//    g.setPaint(Color.red)
    //      g.drawString(collisionCount + "", center.x.toInt, center.y.toInt)
//    g.drawLine(center.x.toInt, center.y.toInt, (center.x + acceleration.x).toInt, (center.y +acceleration.y).toInt)
//    g.drawString(acceleration + "", center.x.toInt, center.y.toInt)
    //    }
  }

  def asEllipsis(drawingSurface: DrawingSurface): Ellipse2D.Double = {
    val doubled: Double = drawingSurface.convertLength(radius * 2)
    val convertX1: Double = drawingSurface.convertWidth(center.x - radius)
    val convertY1: Double = drawingSurface.convertHeight(center.y - radius)
    new Ellipse2D.Double(convertX1, convertY1, doubled, doubled)
  }

  def setGravityPull(finalAcceleration: Acceleration2D): Unit = {
    gravityPull = finalAcceleration
  }

  def mass: Double = {
    massIn match {
      case Some(mass) => mass.toKilograms
      case None => (radius * radius).toSquareMeters
    }
  }

  override def toString: String =
    s"$center ($radius) $acceleration"

  override def equals(any: Any): Boolean = any match {
    case other: Circle => this.id == other.id
    case _ => false
  }

  override def hashCode(): Int = this.id

}
