package prolog

import scala.annotation.targetName

sealed trait Num
case object Zero extends Num
case class Suc[P <: Num]() extends Num

class Plus[A <: Num, B <: Num, Res <: Num]

object Plus:
  given Plus[Zero.type, Zero.type, Zero.type] =  new Plus
  given Plus[A <: Num]: Plus[A, Zero.type, A] = new Plus
  @targetName("plus_zero_b")
  given Plus[B <: Num]: Plus[Zero.type, B, B] = new Plus
  given Plus[A <: Num, B <: Num, Res <: Num](using Plus[A, B, Res]): Plus[A, Suc[B], Suc[Res]] = new Plus

import Plus.given

def sum[A <: Num, B <: Num, Res <: Num](a: A, b: B)(using Plus[A, B, Res]): Res = ???

val one: Suc[Zero.type] = ???
val two: Suc[Suc[Zero.type]] = ???
def res = sum(one, two) // Suc[Suc[Suc[Zero.type]]]
