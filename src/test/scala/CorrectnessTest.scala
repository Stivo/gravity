import com.github.stivo.gravity._
import org.scalatest.{FunSpec, Matchers}
import squants.space.{SquareKilometers, Kilometers}
import squants.space.AreaConversions._
import squants.time.{Minutes, Hours, Days}
import org.scalatest.Matchers._

/**
 * Created by Stivo on 18.01.2016.
 */
class CorrectnessTest extends FunSpec with Matchers {

  describe("Orbits should be correct for ") {
    it("earth") {
      val space: Space = new Space(new DrawingSurface, Days(1))
      val earth = SolarSystem.earth
      space.addBodies(List(SolarSystem.sun, earth))
      val earthCircleOption: Option[Circle] = space.circleForBody(earth)
      earthCircleOption shouldBe 'defined
      val earthCircle = earthCircleOption.get
      val center: Point = earthCircle.center
      for (i <- 1 to 365) {
        space.nextTick()
      }
      space.timePerTick = Hours(6)
      space.nextTick()
      space.time should be (Days(365) + Hours(6))
      val centerAfter: Point = earthCircle.center
      println(f"${center.x.toKilometers}%e / ${center.y.toKilometers}%e")
      println(f"${centerAfter.x.toKilometers}%e / ${centerAfter.y.toKilometers}%e")
      center.distance(centerAfter) should be < Kilometers(1000000)
    }

    it("venus") {
      val space: Space = new Space(new DrawingSurface, Days(1))
      val venus = SolarSystem.venus
      space.addBodies(List(SolarSystem.sun, venus))
      val venusCircleOption: Option[Circle] = space.circleForBody(venus)
      venusCircleOption shouldBe 'defined
      val venusCircle = venusCircleOption.get
      val center: Point = venusCircle.center
      for (i <- 1 to 224) {
        space.nextTick()
      }
      space.timePerTick = Minutes(1009)
      space.nextTick()
      space.time should be (Days(224) + space.timePerTick)
      val centerAfter: Point = venusCircle.center
      println(f"${center.x.toKilometers}%e / ${center.y.toKilometers}%e")
      println(f"${centerAfter.x.toKilometers}%e / ${centerAfter.y.toKilometers}%e")
      println(center.distance(centerAfter).toKilometers)
      center.distance(centerAfter) should be < Kilometers(1000000)
    }
  }


}
