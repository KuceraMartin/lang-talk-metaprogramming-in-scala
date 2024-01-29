package showast

@main
def main =
  val n = 42
  println(Macro.showAst(1 + 2 * n))
