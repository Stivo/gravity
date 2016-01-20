package com.github.stivo.gravity

import java.awt.Graphics2D
import java.awt.event._
import javax.swing._

import squants.space.{Meters, Length}

class DrawingSurface {


  private var xOffset = Meters(0)
  private var yOffset = Meters(0)

  private var displayWidth: Double = 0
  private var displayHeight: Double = 0
  private var g2d: Graphics2D = null
  var minimumDrawingArea = SolarSystem.distanceToSun(SolarSystem.mars)
  var simulationAreaRadius = minimumDrawingArea * 4
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
    xOffset += meterToPixel * xDeltaInPixels * 3
    yOffset -= meterToPixel * yDeltaInPixels * 3
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
    class MoveAction(keyEvent: Int, xDelta: Int, yDelta: Int) extends AbstractAction {
      override def actionPerformed(e: ActionEvent): Unit = {
        move(xDelta, yDelta)
      }
      val name: String = s"$xDelta / $yDelta"
      def install(): Unit = {
        applet.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, name)
        applet.getActionMap().put(name, this)
      }

      def keyStroke: KeyStroke = {
        KeyStroke.getKeyStroke(keyEvent, 0)
      }
    }
    new MoveAction(KeyEvent.VK_UP, 0, 1).install()
    new MoveAction(KeyEvent.VK_DOWN, 0, -1).install()
    new MoveAction(KeyEvent.VK_LEFT, -1, 0).install()
    new MoveAction(KeyEvent.VK_RIGHT, 1, 0).install()
  }


}
