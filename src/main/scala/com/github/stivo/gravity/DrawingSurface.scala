package com.github.stivo.gravity

import java.awt.Graphics2D
import java.awt.event._
import javax.swing._
import javax.swing.event.MouseInputAdapter
import java.awt.{Point => AwtPoint }
import squants.space.{Meters, Length}

class DrawingSurface {

  private var _xOffset = Meters(0)
  private var _yOffset = Meters(0)

  def xOffset: Length = _xOffset
  def yOffset: Length = _yOffset

  private var displayWidth: Double = 0
  private var displayHeight: Double = 0
  private var g2d: Graphics2D = null
  var minimumDrawingArea = SolarSystem.distanceToSun(SolarSystem.mars)
  var simulationAreaRadius: Length = minimumDrawingArea * 20
  var meterToPixel: Length = null

  def setGraphics(g2d: Graphics2D): Unit = {
    displayHeight = g2d.getClipBounds().height
    displayWidth = g2d.getClipBounds().width
    val minimumPixels = Math.min(displayHeight, displayWidth)
    meterToPixel = minimumDrawingArea * 2 / minimumPixels
    this.g2d = g2d
  }
  
  def changeZoom(notches: Int) = {
    minimumDrawingArea += minimumDrawingArea * 0.05 * notches
  }

  def isInWidth(x: Length) = {
    -simulationAreaRadius < x && x < simulationAreaRadius
  }

  def isInHeight(x: Length) = {
    -simulationAreaRadius < x && x < simulationAreaRadius
  }

  def move(xDeltaInPixels: Int, yDeltaInPixels: Int): Unit = {
    _xOffset += meterToPixel * xDeltaInPixels
    _yOffset -= meterToPixel * yDeltaInPixels
  }

  def convertWidth(x: Length): Int = {
    val middle = displayWidth / 2
    (middle + (x - xOffset) / meterToPixel).toInt
  }

  def convertHeight(y: Length): Int = {
    val middle = displayHeight / 2
    (middle + (y - yOffset)  / meterToPixel).toInt
  }

  def convertRadius(length: Length): Double = {
    Math.max(5, length / meterToPixel)
  }

  def addListeners(applet: JPanel) = {
    applet.addMouseWheelListener(new MouseWheelListener {
      override def mouseWheelMoved(e: MouseWheelEvent): Unit = {
        changeZoom(e.getWheelRotation)
      }
    })

    val adapter: MouseInputAdapter = new MouseInputAdapter {
      var origin: AwtPoint = null
      var xOffsetAtStart = xOffset
      var yOffsetAtStart = yOffset

      override def mousePressed(e: MouseEvent): Unit = {
        origin = e.getPoint
        xOffsetAtStart = _xOffset
        yOffsetAtStart = _yOffset
      }

      override def mouseDragged(e: MouseEvent): Unit = {
        val dx = e.getX() - origin.x
        val dy = e.getY() - origin.y
        _xOffset = xOffsetAtStart
        _yOffset = yOffsetAtStart
        move(-dx, dy)
      }
    }
    applet.addMouseMotionListener(adapter)
    applet.addMouseListener(adapter)
  }


}
