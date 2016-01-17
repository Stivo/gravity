/**
 * Created by Stivo on 17.01.2016.
 */
object Utils {
  implicit class DoubleWithExp(d: Double) {
    def **(other: Double) = Math.pow(d, other)
  }

}
