package unrolledpow

import scala.quoted.*

object Macro:
  inline def unrolledPow10(x: Double): Double =
    ${ unrolledPowerImpl('x, 10) }

  private def unrolledPowerImpl(x: Expr[Double], n: Int)(using Quotes): Expr[Double] =
    if n == 0 then '{ 1.0 }
    else if n == 1 then x
    else '{ $x * ${ unrolledPowerImpl(x, n - 1) } }
