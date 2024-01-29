package printf

import scala.compiletime.ops.string.*

type ArgsType[S <: String] <: Tuple = S match
  case "" => EmptyTuple
  case _ => CharAt[S, 0] match
    case '%' => CharAt[S, 1] match
      case 'd' => Int *: ArgsType[Substring[S, 2, Length[S]]]
      case 's' => String *: ArgsType[Substring[S, 2, Length[S]]]
    case _ => ArgsType[Substring[S, 1, Length[S]]]

def printf(s: String)(args: ArgsType[s.type]): Unit =
  val res = args.toList.foldLeft(s): (acc, arg) =>
    val i = acc.indexOf('%')
    acc.patch(i, arg.toString, 2)
  println(res)
    
@main
def main =
  printf("The %s is %d")("answer", 42)
