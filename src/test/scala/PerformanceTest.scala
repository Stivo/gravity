import com.github.stivo.gravity.calculation._
import com.github.stivo.gravity.{DrawingSurface, Space}
import com.janosgyerik.microbench.api.BenchmarkRunner
import com.janosgyerik.microbench.api.annotation.{Benchmark, MeasureTime}
import org.scalatest.{FunSpec, Matchers}

import scala.util.Random

/**
 * Created by Stivo on 18.01.2016.
 */
object PerformanceTest extends App {

  for (calc <- List[GravityCalculator](
    new NaiveGravityCalculator(parallel = false),
    new NaiveWhileGravityCalculator(),
    new NaiveGravityCalculator(parallel = true),
    new NaiveDoubleGravityCalculator(parallel = false),
    new NaiveDoubleGravityCalculator(parallel = true)
  )) {

    for (x <- List(500, 1000, 2000)) {
      println(s"Now running $x with ${calc.getClass.getSimpleName}")
      BenchmarkRunner.run(new OneTick(x, calc))
    }
  }

  @Benchmark(warmUpIterations = 2, iterations = 3)
  class OneTick(planets: Int, gravityCalculator: GravityCalculator) {
    val space: Space = new Space(new DrawingSurface, gravityCalculator = gravityCalculator, random = new Random(0))
    space.addCircles(planets)

    @MeasureTime
    def computeVelocities(): Unit = {
      space.updateVelocities()
    }

  }

}
