package com.github.stivo.gravity

import java.awt.Graphics2D

import squants.space.{Meters, Length}

class DrawingSurface {
  private var displayWidth: Double = 0
  private var displayHeight: Double = 0
  private var g2d: Graphics2D = null
  var lowestX: Length = Meters(-20)
  var highestX: Length = Meters(20)

  def setGraphics(g2d: Graphics2D): Unit = {
    displayHeight = g2d.getClipBounds().height
    displayWidth = g2d.getClipBounds().width
    this.g2d = g2d
  }

  def isInWidth(x: Length) = {
    val convert1: Int = convert(x)
    0 <= convert1 && convert1 <= displayWidth
  }

  def isInHeight(x: Length) = {
    val convert1: Int = convert(x)
    0 <= convert1 && convert1 <= displayHeight
  }

  def convert(x: Length): Int = {
    val int: Int = Math.round(((x - lowestX) / (highestX - lowestX)) * displayHeight).toInt
    println(s"Converted $x to $int, $displayHeight, $displayWidth")
    int
  }

}
