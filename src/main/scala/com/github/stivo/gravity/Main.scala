package com.github.stivo.gravity

import java.awt._
import java.awt.event._
import javax.swing._

import com.github.stivo.gravity.calculation.{StandardGravityCalculator, NaiveDoubleGravityCalculator, NaiveWhileGravityCalculator, NaiveGravityCalculator}
import squants.space.Meters
import squants.time.{Hours, Days, Milliseconds}
import Utils._
import scala.util.Random

/**
 * Created by Stivo on 17.01.2016.
 */
object Main {
  val label: JLabel = new JLabel()
  var break: Boolean = false
  val drawingSurface = new DrawingSurface()

  //  val stopWatch = new StopWatch
  def main(args: Array[String]) {
    val f: JFrame = new JFrame("ShapesDemo2D")
    f.addWindowListener(new WindowAdapter() {
      override def windowClosing(e: WindowEvent) {
        System.exit(0)
      }
    })
    val applet: CircleApplet = new CircleApplet
    f.getContentPane.add("Center", applet)
    val panel: JPanel = new JPanel()
    panel.setLayout(new BorderLayout())
    panel.add("Center", label)
    val pauseButton = new Button("Pause")
    pauseButton.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        break = !break
      }
    })
    panel.add("West", pauseButton)
    f.getContentPane.add("South", panel)
    applet.init
    f.pack
    f.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH)
    f.setVisible(true)
    val updateThread = new Thread {
      override def run(): Unit = {
        while (true) {
          Thread.sleep(10)
          if (!break) {
            StopWatch.reset()
            StopWatch.start("Computing gravity")
            applet.space.nextTick()
            StopWatch.start("Detect collisions")
            applet.space.applyCollisions()
            SwingUtilities.invokeLater(new Runnable {
              override def run(): Unit =
                applet.repaint()
            })
          }
        }
      }
    }
    updateThread.setDaemon(true)
    updateThread.start()
  }
}

class CircleApplet extends JPanel {
  //  val stopWatch = new StopWatch

  val space = new Space(Main.drawingSurface, gravityCalculator = new NaiveDoubleGravityCalculator())
  val frameCounter = new FrameCounter()

//  space.circles :+= new Circle(new Point(Meters(0), Meters(0)), Main.drawingSurface.simulationAreaRadius / 80, color = Color.yellow)

  addAction(this, KeyEvent.VK_S, space.addBodies(SolarSystem.bodies))
  addAction(this, KeyEvent.VK_A, space.addCircles(50))
  addAction(this, KeyEvent.VK_A, space.addCircles(1000), InputEvent.SHIFT_DOWN_MASK)

//  space.addBodies(SolarSystem.bodies)
//  space.addCircles(1000)

  Main.drawingSurface.addListeners(this)

  def init(): Unit = {
    setBackground(Color.black)
    setForeground(Color.white)
  }

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
//    StopWatch.start("Painting self")
    val g2d: Graphics2D = g.create().asInstanceOf[Graphics2D]
    Main.drawingSurface.setGraphics(g2d)
    g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
    g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE)
    g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
    val width: Int = g2d.getClipBounds.getWidth.toInt
    val height: Int = g2d.getClipBounds.getHeight.toInt
    g2d.setColor(Color.black)
    g2d.fillRect(0, 0, width, height)
    space.drawTo(g2d)
    frameCounter.addTick()

    var info: String = f"Time: ${space.time.toDays}%.1f Circles: ${space.circles.size}%d, fps: ${frameCounter.framesPerSecond()}%d, "
//    info += StopWatch.finish().mapValues(long => long + "ms").mkString(", ")
    Main.label.setText(info)
    g2d.dispose
  }
}