package kutils

import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Parallel collection utils.
 *
 * @author Holger Brandl
 */


/** A paralleizable version of kotlin.collections.map
 * @param numThreads The number of parallel threads. Defaults to number of cores minus 2
 * @param exec The executor. By exposing this as a parameter, application can share an executor among different parallel
 *             mapping taks.
 */
fun <T, R> Iterable<T>.pmap(numThreads: Int = Runtime.getRuntime().availableProcessors() - 2, exec: ExecutorService = Executors.newFixedThreadPool(numThreads), transform: (T) -> R): List<R> {
    // note default size is just an inlined version of kotlin.collections.collectionSizeOrDefault
    val destination = ArrayList<R>(if (this is Collection<*>) this.size else 10)

    for (item in this) {
        exec.submit({ destination.add(transform(item)) })
    }

    exec.shutdown()
    exec.awaitTermination(1, TimeUnit.DAYS)

    return destination
}