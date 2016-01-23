//package com.github.stivo.gravity.calculation
//
//import java.awt.Color
//
//import com.github.stivo.gravity.Circle
//
//
//class RecursiveCollisionApplier extends CollisionApplier with FastCircleUser {
//  type T = FastCircle
//  type Coll[T] = Vector[T]
//
//  def empty: Coll[T] = Vector.empty
//
//  var currentCircles: Map[Int, FastCircle] = null
//
//
//  class Quadrant(
//                  val xLowYLow: Coll[T],
//                  val xLowYHigh: Coll[T],
//                  val xHighYLow: Coll[T],
//                  val xHighYHigh: Coll[T]
//                  ) {
//    xLowYLow.foreach(_.circle.color = Color.red)
//    xLowYHigh.foreach(_.circle.color = Color.yellow)
//    xHighYLow.foreach(_.circle.color = Color.green)
//    xHighYHigh.foreach(_.circle.color = Color.blue)
//    private val xLowYLowSet: Set[T] = xLowYLow.toSet
//    private val xLowYHighSet: Set[T] = xLowYHigh.toSet
//    private val xHighYLowSet: Set[T] = xHighYLow.toSet
//    private val xHighYHighSet: Set[T] = xHighYHigh.toSet
//    private val intersectXLow: Set[T] = xLowYLowSet.intersect(xLowYHighSet)
//    private val intersectXHigh: Set[T] = xHighYLowSet.intersect(xHighYHighSet)
//    private val intersectYLow: Set[T] = xLowYLowSet.intersect(xHighYLowSet)
//    private val intersectYHigh: Set[T] = xLowYHighSet.intersect(xHighYHighSet)
//    private val intersectAll: Set[T] =
//      intersectXLow
//        .intersect(intersectXHigh)
//        .intersect(intersectYLow)
//        .intersect(intersectYHigh)
//    intersectAll.foreach(_.circle.color = Color.white)
//  }
//
//  object Quadrant {
//    def apply(circles: Vector[FastCircle],
//              xLine: Double = 0,
//              yLine: Double = 0,
//              xLow: Double = Double.NegativeInfinity,
//              xHigh: Double = Double.PositiveInfinity,
//              yLow: Double = Double.NegativeInfinity,
//              yHigh: Double = Double.PositiveInfinity): Quadrant = {
//      var xLowYLow: Coll[T] = empty
//      var xLowYHigh: Coll[T] = empty
//      var xHighYLow: Coll[T] = empty
//      var xHighYHigh: Coll[T] = empty
//
//      circles
//        .withFilter(circle => circle.xLow >= xLow && circle.xHigh <= xHigh)
//        .withFilter(circle => circle.yLow >= yLow && circle.yHigh <= yHigh)
//        .foreach { circle =>
//        if (circle.xHigh >= xLine && circle.xLow <= xHigh) {
//          if (circle.yHigh >= yLine && circle.yLow <= yHigh) {
//            xHighYHigh +:= circle
//          }
//          if (circle.yLow <= yLine && circle.yHigh >= yLow) {
//            xHighYLow +:= circle
//          }
//        }
//        if (circle.xLow <= xLine && circle.xHigh >= xLow) {
//          if (circle.yHigh >= yLine && circle.yLow <= yHigh) {
//            xLowYHigh +:= circle
//          }
//          if (circle.yLow <= yLine && circle.yHigh >= yLow) {
//            xLowYLow +:= circle
//          }
//        }
//      }
//      new Quadrant(xLowYLow, xLowYHigh, xHighYLow, xHighYHigh)
//    }
//  }
//
//  override def detectCollisions(circles: Iterable[Circle]): CollisionGroups = {
//    createFastCircles()
//  }
//}
//
//
//
