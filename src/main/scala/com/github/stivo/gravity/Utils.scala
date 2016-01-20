package com.github.stivo.gravity

import java.awt.event.ActionEvent
import javax.swing.{KeyStroke, JComponent, JPanel, AbstractAction}

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
  implicit def function2Action(func: => Unit) = new AbstractAction() {
    override def actionPerformed(e: ActionEvent): Unit = func
  }

  private var counter = 0
  private def nextName() = {
    counter += 1
    s"ACTION_$counter"
  }

  def addAction(panel: JPanel, keyEvent: Int, func: => Unit, name: String = nextName()): Unit = {
    panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyEvent, 0), name)
    panel.getActionMap.put(name, function2Action(func))
  }

}
