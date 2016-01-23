package com.github.stivo.gravity.calculation

import com.github.stivo.gravity.{Speed2D, Circle}
import squants.time.Time

trait GravityCalculator {

  def calculateForceVectors(circles: IndexedSeq[Circle], timePerTick: Time): IndexedSeq[Speed2D] = {
    circles match {
      case Seq() => IndexedSeq.empty
      case Seq(x) => IndexedSeq(Speed2D())
      case x => calculateForceVectorsForMoreThanOne(x, timePerTick)
    }
  }

  def calculateForceVectorsForMoreThanOne(circles: IndexedSeq[Circle], timePerTick: Time): IndexedSeq[Speed2D]

}
