---
title: Type-safe- and metaprogramming in Scala
author: "[Martin Kučera](mailto:martin@mkucera.cz) @[Ematiq](https://ematiq.com)"
date: "29. 01. 2024"
slideNumber: false
---

# Martin Kučera
- Bc @ FIT ČVUT
- MSc @ ETH Zürich
- Master's thesis @ EPFL (Type-safe SQL Queries in Scala)
- Scala developer @ Ematiq

# Témata
- metaprogramming overview
- implicits
- match types
  - type-safe printf
- macros
  - type checking Scala code against a database schema

# Ptejte se prosím průběžně

# Co se mi líbí na Scale
- hezká v teorii i v praxi
- jednoduchá, ale přitom expresivní

# Type inference

<div style="width: 45 %; float: left">

```scala
val a: Int = 42
val l: List[String] =
  List[String]("Hello", "world")
```
</div>

<div style="width: 45%; float: right">

```scala
val a = 42
val l =
  List("Hello", "world")
```
</div>

# Term inference (implicits)

```scala
trait Serializer[T]:
  def serialize(x: T): String

def sendToKafka[T](x: T)(using serializer: Serializer[T]):
  val str = serialzer.serialize(x)
  // TODO: send str to Kafka
```

. . .

```scala
case class Message(id: Int, author: String, content: String)

given Serializer[Message] = JsonSerializer.derive

val msg = Message(1, "Martin", "Hello, world!")
sendToKafka(msg)/*(given_Serializer_Message)*/
```

# Metaprogramování

. . .

**Co to je**

Metaprogramming is a programming technique in which<br>
computer programs have the ability to treat other programs<br> as their data. 
(Wikipedia)

. . .

<img src="img/metaprogramming.svg">

# K čemu to je
- redukování boilerplatu
- rozšíření jazyka
- type safety

# Scala & compile time
- implicits
- inline functions
- compile time operations
- match types
- mirrors & deriving
- macros
  - quotes and splices
  - reflection API

# Proč nestačí makra?
- jednoduchost/čitelnost kódu
- type safety!

. . .

## Co se stane, když knihovna používá makra
<img src="img/metaprogramming2.svg">


# Implicits revisited
. . .

Nepřipomíná to Prolog?

. . .

```scala
sealed trait Num
case object Zero extends Num
case class Suc[P <: Num]() extends Num
```

---

```scala
class Plus[A <: Num, B <: Num, Res <: Num]

given Plus[Zero.type, Zero.type, Zero.type] = new Plus

given Plus[A <: Num]: Plus[A, Zero.type, A] = new Plus

given Plus[B <: Num]: Plus[Zero.type, B, B] = new Plus

given Plus[A <: Num, B <: Num, Res <: Num](using Plus[A, B, Res]): Plus[A, Suc[B], Suc[Res]] = new Plus
```

---

```scala
def sum[A <: Num, B <: Num, Res <: Num](a: A, b: B)(using Plus[A, B, Res]): Res = ???

val one: Suc[Zero.type] = ???
val two: Suc[Suc[Zero.type]] = ???
def res = sum(one, two) // Suc[Suc[Suc[Zero.type]]]
```

# Inline functions
```scala
inline val DebugMode = true

inline def debug[T](inline s: Any)(inline op: T): T =
  inline if DebugMode then println(s.toString)
  op
```

# Type-level programming
```scala
import scala.compiletime.ops.int.*

val a: 1 = 1
val b: 2 = 2
val c: a.type + b.type = 3
```

---

```scala
type Square[X <: Int] = X * X
val a: 10 = 10
val b: Square[a.type] = 100
```

# Match types
```scala
type Elem[X] = X match
  case String => Char
  case Array[t] => t
  case Iterable[t] => t
```

. . .

```scala
val xs: List[Int] = ???
val x: Elem[xs.type] = 123
```

# Demo: type-safe printf
Chceme:
```scala
def printf(s: String)(args: ArgsType[s.type]): Unit
```

# Macros
```scala
import scala.quoted.*

object Macro:
  inline def hello: String =
    ${ helloImpl }

  private def helloImpl(using Quotes): Expr[String] =
    '{ "Hello, world!" }
```

. . .

```scala
println(Macro.macro) // Hello, world!
```

# Quotes and splices
quotes: delay execution
```scala
'{ () /* can be arbitrary scala code */ }: Expr[Unit]
```

splices: evaluate, insert into quotes
```scala
${ () /* can be arbitrary scala code */ }: Unit
```

---

```scala
def unrolledPowerImpl(x: Expr[Double], n: Int)(using Quotes): Expr[Double] =
  if n == 0 then '{ 1.0 }
  else if n == 1 then x
  else '{ $x * ${ unrolledPowerImpl(x, n - 1) } }
```

# Reflection API
Dovoluje nám pracovat přímo s AST
```scala
def showAstImpl[T](x: Expr[T])(using Type[T])(using Quotes): Expr[T] =
    import quotes.reflect.*

    val term = x.asTerm
    val tpe = TypeRepr.of[T]

    println(term)
    println(tpe)

    term.asExprOf[T]
```

# Databázová knihovna
cíl:
```scala
def selectFrom(tableName: String, limit: Int): Iterator[?]

val res = selectFrom("artists", 10)
for row <- res do
  println(artist.name) // ok
  println(artist.skhgjs) // error: column skhgjs does not exist
```

# Structural types
```scala
class Record(fields: Map[String, Any]) extends Selectable:
  def selectDynamic(name: String): Any = fields(name)








```

# Structural types
```scala
class Record(fields: Map[String, Any]) extends Selectable:
  def selectDynamic(name: String): Any = fields(name)

val person = Record(Map(
    "name" -> "Emma",
    "age" -> 42,
  ))

println(person.name)
```

# Structural types
```scala
class Record(fields: Map[String, Any]) extends Selectable:
  def selectDynamic(name: String): Any = fields(name)

val person = Record(Map(
    "name" -> "Emma",
    "age" -> 42,
  )).asInstanceOf[Record & { val name: String; val age: Int }]

println(person.name)
```

# Závěr
- I v compile timu je potřeba řešit type safety
- Ve Scale lze metaprogramovat bezpečně, ale i nebezpečně
- další zdroje:
  - příklady z prezentace
  - [Scala dokumentace](https://docs.scala-lang.org/scala3/reference/metaprogramming/index.html)
  - [Scala 3 Compiler Academy](https://www.youtube.com/@scala3compileracademy/videos)
  - [Implementing a Macro](https://youtu.be/dKblZynnhgo?si=0Lc7x1uv8hpbKoL6) (Scala Days 2023 talk)
  - existující projekty:
    - [Magnolia](https://github.com/softwaremill/magnolia)
    - [upickle](https://github.com/com-lihaoyi/upickle)
    - [jsoniter-scala](https://github.com/plokhotnyuk/jsoniter-scala)

[martin@mkucera.cz](mailto:martin@mkucera.cz)<br>
[linkedin.com/in/kuceramartin](https://www.linkedin.com/in/kuceramartin/)
TODO slidy
