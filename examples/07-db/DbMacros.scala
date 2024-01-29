package db

import java.sql.*
import scala.quoted.*
import scala.sys.process.*

object DbMacros:

  transparent inline given[TableName <: String]: TableSchema[TableName] =
    ${ givenTableSchemaImpl[TableName] }

  private def givenTableSchemaImpl[TableName <: String : Type](using Quotes): Expr[TableSchema[TableName]] =
    import quotes.reflect.*

    val ConstantType(StringConstant(tableName)) = TypeRepr.of[TableName]: @unchecked

    // val query = s"SELECT column_name, data_type FROM information_schema.columns WHERE table_name = '$tableName'"
    // type QueryRow = Row & { val name: String; val dataType: String }
    // val statement = StaticConfig.connection.createStatement()
    // val resultSet = statement.executeQuery(query)
    // val result = ResultIterator(resultSet, rs => Row(Map("name" -> rs.getString("column_name"), "dataType" -> rs.getString("data_type"))).asInstanceOf[QueryRow]).toList

    val result = getColumns(tableName)
    assert(result.nonEmpty)

    val columnNames = Expr.ofSeq(result.map(column => Expr(column.name)))
    val rowType = result.foldLeft(TypeRepr.of[Row]): (acc, column) =>
      val tpe = column.dataType match
        case "text" | "character varying" => TypeRepr.of[String]
        case "integer" => TypeRepr.of[Int]
        case _ => throw new Exception(s"Uknown column type ${column.dataType}.")
      Refinement(acc, column.name, tpe)

    rowType.asType match
      case '[t] =>
        '{ new TableSchema[TableName]($columnNames) { type RowType = t } }

  private def getColumns(tableName: String): Seq[DbColumn] =
    val query = s"SELECT column_name, data_type FROM information_schema.columns WHERE table_name = '$tableName'"
    val res = Process(s"psql -d '${DbConfig.uri}' -c \"$query\"").!!
    res.split('\n').drop(2).flatMap: row =>
      val arr = row.split('|').map(_.trim)
      if arr.length == 2 then
        Some(DbColumn(arr(0), arr(1)))
      else
        None

  case class DbColumn(name: String, dataType: String)
