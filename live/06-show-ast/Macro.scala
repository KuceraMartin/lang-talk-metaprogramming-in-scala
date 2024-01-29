package showast

import scala.quoted.* 

object Macro:
  inline def showAst[T](inline x: T): T =
    ${ showAstImpl('x) }

  private def showAstImpl[T](x: Expr[T])(using Type[T])(using Quotes): Expr[T] =
    import quotes.reflect.*

    val term = x.asTerm // TODO: rewrite with reflection
    val tpe = TypeRepr.of[T]

    println(term)
    println(tpe)

    term.asExprOf[T]
