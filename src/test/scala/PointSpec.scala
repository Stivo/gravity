import com.github.stivo.gravity.{Point, Circle}
import org.scalatest.{FunSpec, Matchers}
import squants.space.Meters

/**
 * Created by Stivo on 18.01.2016.
 */
class PointSpec extends FunSpec with Matchers {
  
  describe("Points") {
    describe("should calculate distance correctly for") {
      val center = Point(Meters(0), Meters(0))
      it ("same point") {
        val point2 = Point(Meters(0), Meters(0))
        center.distance(point2) should be(Meters(0))
        val point3 = Point(Meters(5.8), Meters(8.1))
        val point4 = Point(Meters(5.8), Meters(8.1))
        point3.distance(point4) should be(Meters(0))
      }

      it ("different points") {
        val point1 = Point(Meters(5), Meters(0))
        point1.distance(center) should be(Meters(5))
        val point3 = Point(Meters(0), Meters(5))
        point3.distance(center) should be(Meters(5))
      }

      it ("pythagoraic") {
        val point1 = Point(Meters(3), Meters(4))
        point1.distance(center) should be(Meters(5))
      }
    }
  }

}
