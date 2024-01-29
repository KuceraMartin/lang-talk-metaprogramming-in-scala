
package simplemacro

import scala.quoted.*

object Macro:
  inline def hello: String =
    ${ helloImpl }

  private def helloImpl(using Quotes): Expr[String] =
    '{ "Hello, world!" }
