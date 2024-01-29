package printf

import scala.compiletime.ops.string.*

def printf(s: String)(args: Tuple): Unit =
  val res = args.toList.foldLeft(s): (acc, arg) =>
    val i = acc.indexOf('%')
    acc.patch(i, arg.toString, 2)
  println(res)
    
@main
def main =
  printf("The %s is %d")("answer", 42)
