package kutils


import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


interface ParCol<T> : Iterable<T> {

}

internal class ParColImpl<T>(init: Iterable<T>) : ArrayList<T>(0), ParCol<T> {
    init {
        addAll(init)
    }
}

fun <T> Iterable<T>.par(): ParCol<T> {
    return ParColImpl<T>(this);
}


fun <T, R> ParCol<T>.map(numThreads: Int = Runtime.getRuntime().availableProcessors() - 2, exec: ExecutorService = Executors.newFixedThreadPool(numThreads), transform: (T) -> R): ParCol<R> {
    // note default size is just an inlined version of kotlin.collections.collectionSizeOrDefault
    val destination = ArrayList<R>(if (this is Collection<*>) this.size else 10)

    for (item in this) {
        exec.submit { destination.add(transform(item)) }
    }

    exec.shutdown()
    exec.awaitTermination(1, TimeUnit.DAYS)

    return ParColImpl(destination)
}


fun main(args: Array<String>) {
    val totalLength = listOf("test", "sdf", "foo").par().map { it.length }.fold(0, { sum, el -> sum + el })
    println(totalLength)

    //    listOf("test", "sdf", "foo").with {}

}