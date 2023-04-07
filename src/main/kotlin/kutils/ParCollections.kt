package kutils

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Parallel collection mimicking  scala's .par().
 *
 * @author Holger Brandl
 */

/** A delegated tagging interface to allow for parallized extension functions */
class ParCol<T>(val it: Iterable<T>, val executorService: ExecutorService) : Iterable<T> by it


/** Convert a stream into a parallel collection. */
fun <T> Iterable<T>.par(
    numThreads: Int = maxOf(
        Runtime.getRuntime()
            .availableProcessors() - 2, 1
    ),
    executorService: ExecutorService = Executors.newFixedThreadPool(numThreads),
): ParCol<T> {
    return ParCol(this, executorService)
}


/** De-parallelize a collection. Undos <code>par</code> */
fun <T> ParCol<T>.unpar(): Iterable<T> {
    return this.it
}


fun <T, R> ParCol<T>.map(transform: (T) -> R): ParCol<R> {
    val destination = ConcurrentLinkedQueue<R>()

    // http://stackoverflow.com/questions/3269445/executorservice-how-to-wait-for-all-tasks-to-finish
    // use futures here to allow for recycling of the executorservice
    val futures = this.asIterable()
        .map { executorService.submit { destination.add(transform(it)) } }
    futures.map { it.get() } // this will block until all are done

    //    executorService.shutdown()
    //    executorService.awaitTermination(1, TimeUnit.DAYS)

    return ParCol(destination, executorService)
}


/** A parallelized version of kotlin.collections.map
 * @param numThreads The number of parallel threads. Defaults to number of cores minus 2
 * @param exec The executor. By exposing this as a parameter, application can share an executor among different parallel mapping tasks.
 */
fun <T, R> Iterable<T>.parmap(
    numThreads: Int = maxOf(
        Runtime.getRuntime()
            .availableProcessors() - 2, 1
    ),
    exec: ExecutorService = Executors.newFixedThreadPool(numThreads),
    transform: (T) -> R,
): Iterable<R> {
    return par(executorService = exec).map(transform)
        .unpar()
}
