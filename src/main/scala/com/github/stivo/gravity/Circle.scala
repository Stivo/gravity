package com.github.stivo.gravity

import java.awt.geom.Ellipse2D
import java.awt.{Color, Graphics2D}

import com.github.stivo.gravity.Utils._
import squants.mass.{Kilograms, Mass}
import squants.space.{Meters, Area, Length}


object Circle {
  private var id = 0
}

class Circle(var center: Point,
             var radius: Length,
             var acceleration: Speed2D = Speed2D(),
             var color: Color = Color.black,
             var collisionCount: Int = 0,
             var massIn: Option[Mass] = None) {

  val id = {
    Circle.id += 1
    Circle.id
  }

  var gravityPull: Speed2D = Speed2D()

  def withinBounds(drawingSurface: DrawingSurface): Boolean = {
    center.within(drawingSurface)
  }

  def collidesWith(circle2: Circle) = {
    val length: Double = radius.toMeters + circle2.radius.toMeters
    center.distanceToSquared(circle2.center) < length * length
  }

  def updatePosition(): Unit = {
    acceleration = acceleration + gravityPull
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
    val doubled: Double = drawingSurface.convertRadius(radius * 2)
    val convertX1: Double = drawingSurface.convertWidth(center.x - radius)
    val convertY1: Double = drawingSurface.convertHeight(center.y - radius)
    new Ellipse2D.Double(convertX1, convertY1, doubled, doubled)
  }

  def setGravityPull(finalAcceleration: Speed2D): Unit = {
    gravityPull = finalAcceleration
  }

  def mass: Mass = {
    massIn match {
      case Some(mass) => mass
      case None => Kilograms((radius * radius * radius).toCubicMeters)
    }
  }

  override def toString: String =
    f"$center%s (${radius.toMeters}%.1f" // radius) $acceleration"

  override def equals(any: Any): Boolean = any match {
    case other: Circle => this.id == other.id
    case _ => false
  }

  override def hashCode(): Int = this.id

}
