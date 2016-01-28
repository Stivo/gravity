package com.github.stivo.gravity

import java.awt.{Point => AwtPoint, Color, Graphics2D}
import java.awt.event._
import javax.swing._
import javax.swing.event.MouseInputAdapter
import squants.space.{Kilometers, Meters, Length}

class DrawingSurface {

  val config = new Config()

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

  private def updateSimulationRadius() {
    if (simulationAreaRadius < minimumDrawingArea) {
      simulationAreaRadius = minimumDrawingArea
    }
  }

  def changeZoom(notches: Int) = {
    minimumDrawingArea += minimumDrawingArea * 0.05 * notches
    updateSimulationRadius()
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

  def convertXPositionToXPixel(x: Length): Int = {
    val middle = displayWidth / 2
    (middle + (x - xOffset) / meterToPixel).toInt
  }

  def convertYPositionToYPixel(y: Length): Int = {
    val middle = displayHeight / 2
    (middle + (y - yOffset)  / meterToPixel).toInt
  }

  def convertRadius(length: Length): Double = {
    Math.max(3, length / meterToPixel)
  }

  def drawScale() = {
    g2d.setColor(Color.white)
    drawScaleX()
    drawScaleY()
  }

  def drawScaleX(): Unit = {
    val (scaleSize, width) = getLengthOfScale(displayWidth)

    val midX: Int = (displayWidth / 2).toInt
    val y: Int = (displayHeight - 10).toInt
    g2d.drawLine(midX - width, y, midX + width, y)
    g2d.drawString(scaleSize.toString(Kilometers, "%.0e"), midX, y - 5)
  }

  def drawScaleY(): Unit = {
    val (scaleSize, height) = getLengthOfScale(displayHeight)

    val midY: Int = (displayHeight / 2).toInt
    val x: Int = (displayWidth - 10).toInt
    g2d.drawLine(x, midY - height, x, midY + height)
    g2d.drawString(scaleSize.toString(Kilometers,  "%.0e"), x - 30, midY)
  }

  private def getLengthOfScale(pixels: Double): (Length, Int) = {
    val minimumWidth: Length = meterToPixel * pixels * 0.08
    var scaleSize = Meters(1)
    while (scaleSize < minimumWidth) {
      scaleSize *= 10
    }
    val width: Int = (scaleSize / meterToPixel / 2).toInt
    (scaleSize, width)
  }

  def addListeners(applet: JPanel) = {
    applet.addMouseWheelListener(new MouseWheelListener {
      override def mouseWheelMoved(e: MouseWheelEvent): Unit = {
        changeZoom(e.getWheelRotation)
        applet.repaint()
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
        applet.repaint()
      }
    }
    applet.addMouseMotionListener(adapter)
    applet.addMouseListener(adapter)
  }


}

class Config(var drawOrbits: Boolean = false, var drawNames: Boolean = false)