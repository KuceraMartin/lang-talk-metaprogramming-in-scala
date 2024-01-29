//> using dep "org.postgresql:postgresql:42.7.1"

package db

import DbMacros.given
import DbConfig.given

@main
def main =
  val res = selectFrom("tracks", 10)
  for row <- res do
    println(row.position)
