package com.github.stivo.gravity

import java.awt.Graphics2D

import squants.space.{Meters, Length}

class DrawingSurface {

  private var displayWidth: Double = 0
  private var displayHeight: Double = 0
  private var g2d: Graphics2D = null
  var simulationAreaRadius = Meters(40)
  var minimumDrawingArea = Meters(10)
  var meterToPixel: Length = null

  def setGraphics(g2d: Graphics2D): Unit = {
    displayHeight = g2d.getClipBounds().height
    displayWidth = g2d.getClipBounds().width
    val minimumPixels = Math.min(displayHeight, displayWidth)
    meterToPixel = minimumDrawingArea * 2 / minimumPixels
    this.g2d = g2d
  }

  def isInWidth(x: Length) = {
    -simulationAreaRadius < x && x < simulationAreaRadius
  }

  def isInHeight(x: Length) = {
    -simulationAreaRadius < x && x < simulationAreaRadius
  }

  def convertWidth(x: Length): Int = {
    val middle = displayWidth / 2
    (middle + x/ meterToPixel).toInt
  }

  def convertHeight(x: Length): Int = {
    val middle = displayHeight / 2
    (middle + x / meterToPixel).toInt
  }

  def convertLength(length: Length): Double = length / meterToPixel

}
