package com.github.stivo.gravity.calculation

import com.github.stivo.gravity.Circle

import scala.collection.immutable.Set

class CollisionGroups {

  private var map: Map[Int, Set[Circle]] = Map.empty

  def collisionGroups: Iterable[Set[Circle]] = {
    var set: Set[Int] = Set.empty
    var retVal: Vector[Set[Circle]] = Vector.empty
    for {
      (key, value) <- map
      if !set.contains(key)
    } {
      set ++= value.map(_.id)
      retVal +:= value
    }
    retVal
  }

  def addCollision(v1: Circle, v2: Circle): Unit = {
    val oldSet = (map.get(v1.id), map.get(v2.id)) match {
      case (Some(x), None) =>
        x
      case (Some(x), Some(y)) =>
        x ++ y
      case (None, Some(x)) =>
        x
      case (None, None) =>
        Set.empty[Circle]
    }
    val newSet = oldSet + v1 + v2
    for (key <- newSet) {
      map += key.id -> newSet
    }
  }

}
