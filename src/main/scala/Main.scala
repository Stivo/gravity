import java.awt.{Color, Graphics2D, Graphics, Dimension}
import java.awt.event.{ActionEvent, ActionListener, WindowEvent, WindowAdapter}
import javax.swing.{Timer, JPanel, JApplet, JFrame}

/**
 * Created by Stivo on 17.01.2016.
 */
object Main {
  def main(args: Array[String]) {
    val f: JFrame = new JFrame("ShapesDemo2D")
    f.addWindowListener(new WindowAdapter() {
      override def windowClosing(e: WindowEvent) {
        System.exit(0)
      }
    })
    val applet: CircleApplet = new CircleApplet
    f.getContentPane.add("Center", applet)
    applet.init
    f.pack
    f.setSize(new Dimension(550, 100))
    f.setVisible(true)
    new Timer(10, new ActionListener() {
      override def actionPerformed(e: ActionEvent): Unit = {
        applet.circles.tick(applet.getWidth, applet.getHeight)
        applet.repaint()
      }
    }).start()
  }
}

class CircleApplet extends JPanel {

  val circles = new Circles()
  val frameCounter = new FrameCounter()

  def init(): Unit = {
    setBackground(Color.white)
    setForeground(Color.black)
  }

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
    val g2d: Graphics2D = g.create().asInstanceOf[Graphics2D]
    val width: Int = g2d.getClipBounds.getWidth.toInt
    val height: Int = g2d.getClipBounds.getHeight.toInt
    g2d.clearRect(0, 0, width, height)
    circles.circles.foreach(circle =>
      circle.drawTo(g2d)
    )
    frameCounter.addTick()
    g2d.drawString(s"Circles: ${circles.circles.size}, ticks: ${circles.tick}, fps: ${frameCounter.getTicks()}", 0, height - 20)
    g2d.dispose
  }
}