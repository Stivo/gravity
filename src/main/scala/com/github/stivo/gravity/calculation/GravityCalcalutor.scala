package com.github.stivo.gravity.calculation

import com.github.stivo.gravity.{Speed2D, Circle}
import squants.time.Time

/**
 * Created by Stivo on 18.01.2016.
 */
trait GravityCalcalutor {

  def calculateForceVectors(circles: IndexedSeq[Circle], timePerTick: Time): IndexedSeq[Speed2D]

}
