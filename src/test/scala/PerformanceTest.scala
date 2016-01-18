import com.github.stivo.gravity.{DrawingSurface, Space}
import com.janosgyerik.microbench.api.BenchmarkRunner
import com.janosgyerik.microbench.api.annotation.{Benchmark, MeasureTime}
import org.scalatest.{FunSpec, Matchers}

import scala.util.Random

/**
 * Created by Stivo on 18.01.2016.
 */
object PerformanceTest extends App {


  BenchmarkRunner.run(new OneTick(100))
  BenchmarkRunner.run(new OneTick(500))
  BenchmarkRunner.run(new OneTick(1000))
  BenchmarkRunner.run(new OneTick(2000))

  @Benchmark(iterations = 5)
  class OneTick(planets: Int) {
    val space: Space = new Space(new DrawingSurface)
    val random: Random = new Random(0)
    space.addCircles(planets, random)

    @MeasureTime
    def computeVelocities(): Unit = {
      space.updateVelocities()
    }

  }
}
