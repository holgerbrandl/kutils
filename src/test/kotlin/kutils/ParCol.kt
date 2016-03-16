package kutils


import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class ParCol<T>(val it: Iterable<T>) : Iterable<T> by it


/** Convert a stream into a parallel collection. */
fun <T> Iterable<T>.par(): ParCol<T> {
    return ParCol(this);
}


/** De-palatalize a collection. Undos <code>par</code> */
fun <T> ParCol<T>.unpar(): Iterable<T> {
    return this.it;
}


fun <T, R> ParCol<T>.map(numThreads: Int = Runtime.getRuntime().availableProcessors() - 2, exec: ExecutorService = Executors.newFixedThreadPool(numThreads), transform: (T) -> R): ParCol<R> {
    // note default size is just an inlined version of kotlin.collections.collectionSizeOrDefault
    val destination = ArrayList<R>(if (this is Collection<*>) this.size else 10)

    for (item in this) {
        exec.submit { destination.add(transform(item)) }
    }

    exec.shutdown()
    exec.awaitTermination(1, TimeUnit.DAYS)

    return ParCol(destination)
}


fun main(args: Array<String>) {
    val totalLength = listOf("test", "sdf", "foo").par().map { it.length }.fold(0, { sum, el -> sum + el })
    println(totalLength)

    //    listOf("test", "sdf", "foo").with {}
}