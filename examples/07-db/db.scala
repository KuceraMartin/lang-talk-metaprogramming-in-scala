package db

import java.sql.*
import scala.annotation.implicitNotFound


class Row(fields: Map[String, Any]) extends Selectable:

  def selectDynamic(name: String) = fields(name)

end Row


class ResultIterator[R](resultSet: ResultSet, conversion: ResultSet => R) extends Iterator[R]:

	private var hasNextRes: Option[Boolean] = None

	def hasNext: Boolean =
		hasNextRes match
			case Some(res) => res
			case None =>
				hasNextRes = Some(resultSet.next())
				hasNextRes.get
		
	def next(): R = 
		hasNextRes = None
		conversion(resultSet)

end ResultIterator


@implicitNotFound("Table ${TableName} does not exist")
class TableSchema[TableName <: String](val columns: Seq[String]):

  type RowType

  def makeRow(resultSet: ResultSet): Row =
    val fields =
      columns.map(name => name -> resultSet.getObject(name)).toMap
    Row(fields)

end TableSchema


def selectFrom(tableName: String, limit: Int)(using schema: TableSchema[tableName.type])(using connection: Connection): Iterator[schema.RowType] =
  val query = s"SELECT * FROM \"$tableName\" LIMIT $limit"
  val statement = connection.createStatement()
  val resultSet = statement.executeQuery(query)
  ResultIterator(resultSet, schema.makeRow(_).asInstanceOf[schema.RowType])
