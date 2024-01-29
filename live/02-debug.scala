
package debug

inline val DebugMode = true

inline def debug[T](inline s: Any)(inline op: T): T =
  inline if DebugMode then println(s.toString)
  op

@main
def main =
  val x = io.StdIn.readLine().split(' ').toList
  val numerator = x(0).toInt * 2
  val denominator = x(1).toInt + 1
  debug(s"$numerator / $denominator"):
    println(numerator / denominator)
