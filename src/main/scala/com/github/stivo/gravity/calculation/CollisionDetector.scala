package com.github.stivo.gravity.calculation

import com.github.stivo.gravity.Circle

trait CollisionDetector {

  def detectCollisions(circles: Iterable[Circle]): CollisionGroups

}
