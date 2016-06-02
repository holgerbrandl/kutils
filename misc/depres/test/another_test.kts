/**
 * Created by brandl on 2/5/16.
 */


val numbers = listOf(1, 2, 3)
numbers.filter { it > 2 }

val someMap = mapOf(1 to "one", 2 to "two", 3 to "three")

val otherMap: Map<Int, String> = mapOf(1 to "un", 2 to "deux", 3 to "trois")

someMap.keys



data class myclass(val somValue: Int = kutils.testFun()) {

    companion object {
        fun testFun(): Int {
            return 23;
        }
    }
}