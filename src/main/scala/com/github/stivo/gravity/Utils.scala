package com.github.stivo.gravity

import squants.space.{SquareMeters, Length}

/**
 * Created by Stivo on 17.01.2016.
 */
object Utils {
  implicit class DoubleWithExp(d: Double) {
    def **(other: Double) = Math.pow(d, other)
  }
  implicit class IntWithExp(d: Int) {
    def **(other: Double) = Math.pow(d, other)
  }
  implicit class LengthWithExp(d: Length) {
    def **(other: Double) = SquareMeters(Math.pow(d.toMeters, other))
  }

}
