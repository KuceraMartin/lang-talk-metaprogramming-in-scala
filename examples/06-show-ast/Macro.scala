package showast

import scala.quoted.* 

object Macro:
  inline def showAst[T](inline x: T): T =
    ${ showAstImpl('x) }

  private def showAstImpl[T](x: Expr[T])(using Type[T])(using Quotes): Expr[T] =
    import quotes.reflect.*

    // val intSym = Symbol.classSymbol("scala.Int")
    // val term = Apply(
    //   Select(Literal(IntConstant(1)), intSym.methodMember("+")(3)),
    //   List(
    //     Apply(
    //       Select(Literal(IntConstant(2)), intSym.methodMember("*")(3)),
    //       List(Literal(IntConstant(3)))
    //     ),
    //   )
    // )
    val term = x.asTerm
    val tpe = TypeRepr.of[T]

    println(term)
    println(tpe)

    term.asExprOf[T]
