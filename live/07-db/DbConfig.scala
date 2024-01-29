package db

import java.sql.*

object DbConfig:
  val uri = "postgresql://localhost:5432/discogs?user=user&password=1234"
  given connection: Connection = DriverManager.getConnection(s"jdbc:$uri")
