package kutils.kplyr

import java.util.*
import kotlin.comparisons.then

//import

/**
Releated projects
----------------

https://github.com/mikera/vectorz
 * pandas cheat sheet: https://drive.google.com/folderview?id=0ByIrJAE4KMTtaGhRcXkxNHhmY2M&usp=sharing

 */

//abstract class DataCol(name:String, map: List<DataCell>) {}
abstract class DataCol(val name: String) {
//    fun setName(name: String) = {
//        this.name = name
//    }

    abstract infix operator fun plus(dataCol: DataCol): Any


    abstract fun colHash(): IntArray

    abstract val length: Int


//    abstract fun colData() : Any
}


// https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double-array/index.html
// most methods are implemented it in kotlin.collections.plus
class DoubleCol(name: String, val values: DoubleArray) : DataCol(name) {

    override val length = values.size

    override fun colHash(): IntArray = values.map { it.hashCode() }.toIntArray()

    //        return values + values //wrong because it concatenates list and does not plus them
    override fun plus(dataCol: DataCol): DoubleArray = when (dataCol) {
        is DoubleCol -> DoubleArray(values.size, { values[it] }).
                apply { mapIndexed { index, rowVal -> rowVal + dataCol.values[index] } }
        else -> throw UnsupportedOperationException()
    }

//    constructor(name:String, val values: Array<Float>) : this(name, DoubleArray())
}

class IntCol(name: String, val values: IntArray) : DataCol(name) {

    override val length = values.size

    override fun colHash(): IntArray = values.map { it.hashCode() }.toIntArray()

    //        return values + values //wrong because it concatenates list and does not plus them
    override fun plus(dataCol: DataCol): IntArray = when (dataCol) {
        is IntCol -> IntArray(values.size, { values[it] }).
                apply { mapIndexed { index, rowVal -> rowVal + dataCol.values[index] } }
        else -> throw UnsupportedOperationException()
    }

//    constructor(name:String, val values: Array<Float>) : this(name, IntArray())
}

class BooleanCol(name: String, val values: BooleanArray) : DataCol(name) {


    override val length = values.size


    override fun plus(dataCol: DataCol): BooleanArray {
        // todo maybe plus should throw an excption for boolean columns instead of doing a counterintuitive AND??

        //        return values + values //wrong because it concatenates list and does not plus them

        return when (dataCol) {
            is BooleanCol -> BooleanArray(values.size, { values[it] }).
                    apply { mapIndexed { index, rowVal -> rowVal && dataCol.values[index] } }
            else -> throw UnsupportedOperationException()
        }
    }

    override fun colHash(): IntArray = values.map { it.hashCode() }.toIntArray()
//    constructor(name:String, val values: Array<Float>) : this(name, BooleanArray())
}

class StringCol(name: String, val values: List<String>) : DataCol(name) {

    override val length = values.size
//    override fun colData() = values

    override fun colHash(): IntArray = values.map { it.hashCode() }.toIntArray()
//    constructor(name:String, val values: Array<Float>) : this(name, DoubleArray())

    override fun plus(dataCol: DataCol): List<String> {
        if (dataCol is StringCol) {
            return values.zip(dataCol.values).map { it.first + it.second }
        } else {
            throw IllegalArgumentException("non-numeric argument to binary operator")
        }
    }
}

// scalar operations
infix operator fun DoubleArray.plus(i: Int): DoubleArray = map { it + i }.toDoubleArray()

infix operator fun DoubleArray.minus(i: Int): DoubleArray = map { it + i }.toDoubleArray()

//infix operator fun DoubleArray.plus(elements: DoubleArray): DoubleArray {
//    DoubleArray
//    val thisSize = size
//    val arraySize = elements.size
//    val result = Arrays.copyOf(this, thisSize + arraySize)
//    System.arraycopy(elements, 0, result, thisSize, arraySize)
//    return result
//}
//class IntCol(vararg val values: Int) : Column
//class BoolCol(vararg val values: Boolean) : Column


interface DataFrame {
    fun mutate(name: String, formula: (DataFrame) -> Any): DataFrame

    // later support constant mutates as well

//    fun mutate(name: String, formula: (DataFrame) -> Any): DataFrame
//    fun mutate(name: String, formula: (DataFrame) -> DoubleArray): DataFrame
//    fun mutate(name: String, formula: (DataFrame) -> IntArray): DataFrame

    fun arrange(vararg by: String): DataFrame

    // use proper generics here
    fun groupBy(vararg by: String): DataFrame

    fun summarize(name: String, formula: (DataFrame) -> Any): DataFrame

    val nrow: Int

    operator fun get(name: String): DataCol

    // todo use invoke() style operator here (see https://kotlinlang.org/docs/reference/operator-overloading.html)
    fun row(rowIndex: Int): Map<String, Any>

    fun filter(predicate: (DataFrame) -> BooleanArray): DataFrame
}

////    // see http://stackoverflow.com/questions/29268526/how-to-overcome-same-jvm-signature-error-when-implementing-a-java-interface
//@JvmName("mutateString")
//fun DataFrame.mutate(name: String, formula: (DataFrame) -> List<String>): DataFrame {
//    if(this is SimpleDataFrame){
//        return addColumn(StringCol(name, formula(this)))
//    }else
//        throw UnsupportedOperationException()
//}
//
//@JvmName("mutateInt")
//fun DataFrame.mutate(name: String, formula: (DataFrame) -> IntArray): DataFrame {
//    throw UnsupportedOperationException()
//}
//
//@JvmName("mutateDouble")
//fun DataFrame.mutate(name: String, formula: (DataFrame) -> DoubleArray): DataFrame {
//    if(this is SimpleDataFrame){
//        return addColumn(DoubleCol(name, formula(this)))
//    }else
//        throw UnsupportedOperationException()
//}


open class SimpleDataFrame(val cols: List<DataCol>) : DataFrame {

    override fun filter(predicate: (DataFrame) -> BooleanArray): DataFrame {
        val indexFilter = predicate(this)

        return cols.map {
            // subset a colum by the predicate array
            when (it) {
                is DoubleCol -> DoubleCol(it.name, it.values.filterIndexed { index, value -> indexFilter[index] }.toDoubleArray())
                is IntCol -> IntCol(it.name, it.values.filterIndexed { index, value -> indexFilter[index] }.toIntArray())
                is StringCol -> StringCol(it.name, it.values.filterIndexed { index, value -> indexFilter[index] }.toList())
                else -> throw UnsupportedOperationException()
            }
        }.let { SimpleDataFrame(it) }
    }

    override fun row(rowIndex: Int): Map<String, Any> =
            cols.map {
                it.name to when (it) {
                    is DoubleCol -> it.values[rowIndex]
                    is IntCol -> it.values[rowIndex]
                    is StringCol -> it.values[rowIndex]
                    else -> throw UnsupportedOperationException()
                }
            }.toMap()

    // validate that all columns have same length
    private val firstCol = cols.first()


    val ncol = cols.size

    override val nrow by lazy {
        when (firstCol) {
            is DoubleCol -> firstCol.values.size
            is IntCol -> firstCol.values.size
            is StringCol -> firstCol.values.size
            else -> throw UnsupportedOperationException()
        }
    }

    // also provide vararg constructor for convenience
    constructor(vararg cols: DataCol) : this(cols.asList())

    override fun summarize(name: String, formula: (DataFrame) -> Any): DataFrame {
        throw UnsupportedOperationException()
    }

//    https://kotlinlang.org/docs/reference/multi-declarations.html
//    operator fun component1() = 1

    // todo somehow make private to enforce better typed API
    override fun mutate(name: String, formula: (DataFrame) -> Any): DataFrame {

        val mutation = formula(this)

        // expand scalar values to arrays/lists
        val arrifiedMutation: Any = when (mutation) {
            is Int -> IntArray(nrow, { mutation })
            is Double -> DoubleArray(nrow, { mutation })
            is Boolean -> BooleanArray(nrow, { mutation })
            is Float -> FloatArray(nrow, { mutation })
            is String -> Array<String>(nrow) { mutation }.asList()
            else -> mutation
        }


        // unwrap existing columns to use immutable one with given name
//        val mutUnwrapped = {}

        val newCol = when (arrifiedMutation) {
//            is DataCol -> mutation.apply { setName(name) }
            is DoubleArray -> DoubleCol(name, arrifiedMutation)
            is BooleanArray -> BooleanCol(name, arrifiedMutation)
        // todo too weakly typed
            is List<*> -> if (arrifiedMutation.first() is String) StringCol(name, arrifiedMutation as List<String>) else throw UnsupportedOperationException()
            else -> throw UnsupportedOperationException()
        }
        return addColumn(newCol)
    }

    fun addColumn(newCol: DataCol): SimpleDataFrame {
        require(newCol.length == nrow) { "column lengths of data-frame ($nrow) and new column (${newCol.length}) differ" }

        val mutatedCols = cols.toMutableList().apply { add(newCol) }
        return SimpleDataFrame(mutatedCols.toList())
    }


    override operator fun get(name: String): DataCol {
        return cols.find({ it -> it.name == name })!!
    }

    override fun arrange(vararg by: String): DataFrame {

        // utility method to convert columns to comparators
        fun asComparator(by: String): Comparator<Int> {
            val dataCol = this[by]
//            return naturalOrder<*>()
            return when (dataCol) {
                is DoubleCol -> Comparator { left, right -> dataCol.values[left].compareTo(dataCol.values[right]) }
                is IntCol -> Comparator { left, right -> dataCol.values[left].compareTo(dataCol.values[right]) }
                is StringCol -> Comparator { left, right -> dataCol.values[left].compareTo(dataCol.values[right]) }
                is BooleanCol -> Comparator { left, right -> dataCol.values[left].compareTo(dataCol.values[right]) }
                else -> throw UnsupportedOperationException()
            }
        }

        // https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.comparisons/java.util.-comparator/then-by-descending.html
        // https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.comparisons/index.html
        val compChain = by.map { asComparator(it) }.let { it.drop(1).fold(it.first(), { a, b -> a.then(b) }) }


        // see http://stackoverflow.com/questions/11997326/how-to-find-the-permutation-of-a-sort-in-java
        val permutation = (0..(nrow - 1)).sortedWith(compChain).toIntArray()

        // apply permutation to all columns
        return cols.map {
            when (it) {
                is DoubleCol -> DoubleCol(it.name, DoubleArray(nrow, { index -> it.values[permutation[index]] }))
                is IntCol -> IntCol(it.name, IntArray(nrow, { index -> it.values[permutation[index]] }))
                is StringCol -> StringCol(it.name, Array(nrow, { index -> it.values[permutation[index]] }).toList())
                is BooleanCol -> BooleanCol(it.name, BooleanArray(nrow, { index -> it.values[permutation[index]] }))
                else -> throw UnsupportedOperationException()
            }
        }.let { SimpleDataFrame(it) }

    }


    // use proper generics here
    override fun groupBy(vararg by: String): DataFrame {
        //take all grouping columns
        val groupCols = cols.filter { by.contains(it.name) }

        // calculate selector index using hashcode // todo use more efficient scheme to avoid hashing of ints
        val rowHashes: IntArray = groupCols.map { it.colHash() }.foldRight(IntArray(nrow).apply { fill(1) }, IntArray::plus)


        // use filter index for each selector-index
        // see https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/filter-indexed.html

        // and  split up orignal dataframe columns by selector index


        val groupIndicies = rowHashes.
                mapIndexed { index, group -> Pair(index, group) }.
                groupBy { it.first }.
                map {
                    val groupRowIndices = it.value.map { it.second }.toIntArray()
                    GroupIndex(GroupKey(it.key, this.row(groupRowIndices.first())), groupRowIndices)
                }


        fun extractGroup(col: DataCol, groupIndex: GroupIndex): DataCol {
            return when (col) {
                is DoubleCol -> DoubleCol(col.name, col.values.filterIndexed { index, d -> groupIndex.rowIndices.contains(index) }.toDoubleArray())
                is IntCol -> IntCol(col.name, col.values.filterIndexed { index, d -> groupIndex.rowIndices.contains(index) }.toIntArray())
                is StringCol -> StringCol(col.name, col.values.filterIndexed { index, d -> groupIndex.rowIndices.contains(index) }.toList())
                else -> throw UnsupportedOperationException()
            }
        }

        fun extractSubframeByIndex(groupIndex: GroupIndex, df: SimpleDataFrame): SimpleDataFrame {
            val grpSubCols = df.cols.map { extractGroup(it, groupIndex) }
            return SimpleDataFrame(grpSubCols)
        }


        return GroupedDataFrame(groupIndicies.map { it to extractSubframeByIndex(it, this) }.toMap())
    }


}

// enrich group index with actual value tuple
data class GroupKey(val groupHash: Int, val groupTuple: Map<String, Any> = emptyMap()) // we could use distinct here on the key attributes

data class GroupIndex(val group: GroupKey, val rowIndices: IntArray)


internal class GroupedDataFrame(private val groups: Map<GroupIndex, DataFrame>) : DataFrame {
    override fun filter(predicate: (DataFrame) -> BooleanArray): DataFrame {
        throw UnsupportedOperationException()
    }

    override fun mutate(name: String, formula: (DataFrame) -> Any): DataFrame {
        throw UnsupportedOperationException()
    }
//    override fun mutate(name: String, formula: (DataFrame) -> Any?): DataFrame {
//        throw UnsupportedOperationException()
//    }

    override fun row(rowIndex: Int): Map<String, Any> {
        // find the group
//        groups.filterKeys { it.rowIndices.contains(rowIndex) }.values.
        throw UnsupportedOperationException()
    }

    override val nrow: Int
        get() = throw UnsupportedOperationException()

    override fun summarize(name: String, formula: (DataFrame) -> Any): DataFrame {
        throw UnsupportedOperationException()
    }


    override fun arrange(vararg by: String): DataFrame {
        throw UnsupportedOperationException()
    }

    override fun groupBy(vararg by: String): DataFrame {

        throw UnsupportedOperationException()
    }

    override fun get(name: String): DataCol {
        // kplyr/CoreVerbsTest.kt:7
        throw UnsupportedOperationException()
    }
}

// utiltiy functions

fun DataFrame.head(numRows: Int = 5) = filter { IntCol("dummy", rowNumber()) lt numRows }
fun DataFrame.tail(numRows: Int = 5) = filter { IntCol("dummy", rowNumber()) gt (nrow - 5) }
fun DataFrame.rowNumber() = (1..nrow).asSequence().toList().toIntArray()

// add more formatting options here
fun DataFrame.print(colNames: Boolean = true, sep: String = "\t") {
    // todo add support for grouped data here

    if (this !is SimpleDataFrame) {
        return
    }

    if (colNames) this.cols.map { it.name }.joinToString(sep).apply { println(this) }

    rowNumber().map { row(it - 1).values.joinToString(sep).apply { println(this) } }
}

// add more formatting options here
fun DataFrame.glimpse(sep: String = "\t") {
    // todo add support for grouped data here
    if (this !is SimpleDataFrame) {
        return
    }

    val topN = head(8) as SimpleDataFrame

    for (col in topN.cols) {
        val examples = when (col) {
            is DoubleCol -> col.values.toList()
            is IntCol -> col.values.toList()
            is StringCol -> col.values
            else -> throw UnsupportedOperationException()
        }.joinToString(", ").apply { println(this) }

        println(col.name + "\t: " + examples)
    }
}


// vectorized boolean operators
infix fun BooleanArray.AND(other: BooleanArray): BooleanArray = mapIndexed { index, first -> first && other[index] }.toBooleanArray()

infix fun BooleanArray.OR(other: BooleanArray): BooleanArray = mapIndexed { index, first -> first || other[index] }.toBooleanArray()
infix fun BooleanArray.XOR(other: BooleanArray): BooleanArray = mapIndexed { index, first -> first == other[index] }.toBooleanArray()

infix fun DataCol.gt(i: Int): BooleanArray = when (this) {
    is DoubleCol -> this.values.map({ it > i }).toBooleanArray()
    is IntCol -> this.values.map({ it > i }).toBooleanArray()
    else -> throw UnsupportedOperationException()
}

infix fun DataCol.lt(i: Int): BooleanArray = when (this) {
    is DoubleCol -> this.values.map({ it < i }).toBooleanArray()
    is IntCol -> this.values.map({ it < i }).toBooleanArray()
    else -> throw UnsupportedOperationException()
}

infix fun DataCol.eq(i: Any): BooleanArray = when (this) {
    is DoubleCol -> this.values.map({ it == i }).toBooleanArray()
    is IntCol -> this.values.map({ it == i }).toBooleanArray()
    is StringCol -> this.values.map({ it == i }).toBooleanArray()
    else -> throw UnsupportedOperationException()
}

fun main(args: Array<String>) {
    val df = SimpleDataFrame(DoubleCol("test", listOf(2.toDouble(), 3.toDouble(), 1.toDouble()).toDoubleArray()))

    var newDf = df.mutate("new_attr", { it["test"] + it["test"] })
    // also support varargs with
//    var newDf = df.mutate({"new_attr" to  ( it["test"] + it["test"] )})

    // this works
//    val function: (df:DataFrame) -> List<String> = { listOf<String>("test") }
//    newDf = newDf.mutate("category", function)

    newDf = newDf.mutate("category", { "test" })
    newDf.print()

    newDf = newDf.arrange("new_attr")

    newDf.print()

    newDf = newDf.filter({ it["test"] eq 1 })


//            .groupBy().mutate(it.grp.)

    val groupedDf = newDf.groupBy("new_attr")

    groupedDf.arrange("test").summarize("mean_val", { (it["test"] as DoubleCol).values.sum() })

//      var df = DataFrame.fromTsv()
//    var df = DataFrame(listOf(*cols, DataCol(name, rows.map { formula(it) })))
//
//    val df2 = df.mutate("sdf", {it["sdfd"] + it["sdfdf"]);
//    (("x"->).arrange(desc("baum"))

}





