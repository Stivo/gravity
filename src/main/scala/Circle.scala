import java.awt.geom.{Rectangle2D, Point2D, Ellipse2D}
import java.awt.{Graphics2D, Color}
import Utils._


object Circle {
  private var id = 0
}

class Circle(var center: Point,
             var radius: Double,
             val acceleration: Acceleration = Acceleration(0, 0),
             var color: Color = Color.black,
             var collisionCount: Int = 0) {

  val id = {
    Circle.id += 1
    Circle.id
  }

  var gravityPull: Acceleration = Acceleration()

  def withinBounds(width: Int, height: Int): Boolean = {
    center.within(width, height)
  }

  def collidesWith(circle2: Circle) = {
    if (circle2.asEllipsis.intersects(center.x, center.y, radius, radius)) {
      color = Color.red
      circle2.color = Color.red
      true
    } else {
      false
    }
  }

  def mergeWith(other: Circle): Circle = {
    val newMass = mass + other.mass
    val newRadius = Math.sqrt(newMass)
    val newAcceleration = (acceleration * mass + other.acceleration * mass) / newMass
    val newCenter = this.center + (this.center - other.center) * (other.mass / newMass)
    new Circle(
      center,
      newRadius,
      newAcceleration,
      collisionCount = Math.max(collisionCount, other.collisionCount) + 1
    )
  }

  def updatePosition(): Unit = {
    center = center.+(acceleration + gravityPull / mass)
  }

  def drawTo(g: Graphics2D): Unit = {
    g.setPaint(color)
    g.fill(asEllipsis)
    if (this.collisionCount > 0) {
      g.setPaint(Color.yellow)
      g.drawString(collisionCount + "", center.x.toInt, center.y.toInt)
    }
  }

  def asEllipsis: Ellipse2D.Double = {
    val doubled: Double = radius * 2
    new Ellipse2D.Double(center.x - radius, center.y - radius, doubled, doubled)
  }

  def setGravityPull(finalAcceleration: Acceleration): Unit = {
    gravityPull = finalAcceleration
  }

  def mass: Double = radius ** 2

  override def toString: String =
    s"$center ($radius) $acceleration"

  override def equals(any: Any): Boolean = any match {
    case other: Circle => this.id == other.id
    case _ => false
  }

  override def hashCode(): Int = this.id

}
