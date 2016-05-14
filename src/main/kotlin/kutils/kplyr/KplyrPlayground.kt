package kutils.kplyr


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

