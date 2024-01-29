package db

import java.sql.*
import scala.annotation.implicitNotFound

def selectFrom(tableName: String, limit: Int)(using connection: Connection): Iterator[?] =
  val query = s"SELECT * FROM \"$tableName\" LIMIT $limit"
  val statement = connection.createStatement()
  val resultSet = statement.executeQuery(query)
  ???
