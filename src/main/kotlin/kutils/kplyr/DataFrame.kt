package kutils.kplyr


//abstract class DataCol(name:String, map: List<DataCell>) {}
abstract class DataCol(var name: String) {
    fun setName(name: String) = {
        this.name = name
    }

    abstract infix operator fun plus(dataCol: DataCol): DataCol
}


class DoubleCol(name: String, val values: List<Float>) : DataCol(name) {
    override fun plus(dataCol: DataCol): DataCol {
        return DoubleCol(name, values = values.map { it + 2 })
    }
}


//class IntCol(vararg val values: Int) : Column
//class BoolCol(vararg val values: Boolean) : Column


open class DataFrame(vararg listOf: DataCol) {

    //    val rows: List<DataRow> = emptyList()
    val cols: Array<DataCol> = emptyArray()


//    https://kotlinlang.org/docs/reference/multi-declarations.html
//    operator fun component1() = 1

    fun mutate(name: String, formula: (DataFrame) -> DataCol): DataFrame {
        return DataFrame(*cols, formula(this).apply { setName(name) })
    }


    operator fun get(name: String): DataCol {
        return cols.toList().find({ it -> it.name == name })!!
    }

    fun arrange(vararg by: String): Any {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // use proper generics here
    fun groupBy(vararg by: String): GroupedDataFrame = GroupedDataFrame(listOf(this))

}


fun main(args: Array<String>) {
    val df = DataFrame(DoubleCol("test", listOf(1.toFloat(), 2.toFloat(), 3.toFloat())))

    val newDf = df.mutate("new_attr", { it["test"] + it["test"] })
//            .groupBy().mutate(it.grp.)

    newDf.groupBy("new_attr").arrange("test").summarize()

//      var df = DataFrame.fromTsv()
//    var df = DataFrame(listOf(*cols, DataCol(name, rows.map { formula(it) })))
//
//    val df2 = df.mutate("sdf", {it["sdfd"] + it["sdfdf"]);
//    (("x"->).arrange(desc("baum"))

}


// todo make internal
class GroupedDataFrame(private val groups: List<DataFrame>) {
    fun arrange(s: String): GroupedDataFrame {
        //implement me
        return this
    }

    fun summarize(): DataFrame {
        return groups.first()
    }
}

// not good since we want to do vector math where possible
//
//interface DataCell {
//
//}
//
//class DoubleCell(val x: Double) : DataCell {
//
//}
//
//class StringCell(val x: String) : DataCell {
//
//}


//abstract class  DataRow {
//    val parent = df;
//
//    operator fun get(name: String): Any {
//        // create corresponding daceslls depneind gon data tyype
//        when(parent[name]) {
//            is DoubleCol-> DoubleCell(it[])
//        }
//    }
//}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//ad hoc objects https://kotlinlang.org/docs/reference/object-declarations.html#object-expressions
// would be nice but are highly unlikely to work
//fun main(args: Array<String>) {
//
//    val dataFrame = object : DataFrame() {
//        val x = Factor("sdf", "sdf", "sdfd")
//        val y = DblCol(Double.MAX_VALUE, Double.MIN_VALUE)
//        val z = y + y
//    }
//
//
//    val newTable = with(dataFrame) {
//        object : DataFrame() {
//            val src = dataFrame
//
//            val newCol = z + z
//            val base = dataFrame
//        }
//    }
//
//    newTable.newCol
//    newTable.src.x
//
//}

//    fun join(df1: DataFrame, df2: DataFrame, by: (Int) -> Boolean, full: Boolean = false): {
//        df1.rows().map()
//        df1.getRow()
//    }
//}



