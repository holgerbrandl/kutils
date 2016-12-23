package de.mpicbg.scicomp.kutils

/**
 * Collection utils that I missed or are missing in Kotlin's stdlib.
 * @author Holger Brandl
 */

//see https://discuss.kotlinlang.org/t/sliding/120/3
fun <T> List<T>.sliding(windowSize: Int): List<List<T>> {
    return this.dropLast(windowSize - 1).mapIndexed { i, s -> this.subList(i, i + windowSize) }
}

fun <T> Sequence<T>.splitAt(n: Int) = this.toList().let { Pair(it.drop(it.size / 2), it.take(it.size / 2)) }

//see http://stackoverflow.com/questions/34498368/kotlin-convert-large-list-to-sublist-of-set-partition-size
fun <T> Iterable<T>.batch(n: Int): Sequence<List<T>> {
    return BatchingSequence(this, n)
}

internal class BatchingSequence<T>(val source: Iterable<T>, val batchSize: Int) : Sequence<List<T>> {
    override fun iterator(): Iterator<List<T>> = object : AbstractIterator<List<T>>() {
        val iterate = if (batchSize > 0) source.iterator() else emptyList<T>().iterator()
        override fun computeNext() {
            if (iterate.hasNext()) setNext(iterate.asSequence().take(batchSize).toList())
            else done()
        }
    }
}

// alternative batch implementation;
// see https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/window-sliding.md
// also see http://stackoverflow.com/questions/34498368/kotlin-convert-large-list-to-sublist-of-set-partition-size/41301362#41301362
//{
//    fun <T> Iterable<T>.batch(chunkSize: Int) =
//            mapIndexed { i, item -> i to item }.// create index value pairs
//                    groupBy { it.first / chunkSize }.// create grouping index
//                    map { it.value.map { it.second } }   // split into different partitions
//
//
//    val items = (1..100).map { "foo_${it}" }
//
//    items.batch(3)
//}



/** Buffered iterators are iterators which provide a method `head`
 *  that inspects the next element without discarding it.
 *
 *  This method is a simple conversion from scala's collection API
 *  @see [[scala.collection.BufferedIterator]]
 */
class BufferedIterator<A>(val wrappedIt: Iterator<A>) {

    var hd: A? = null
    var hdDefined: Boolean = false

    /** Returns next element of iterator without advancing beyond it. */
    fun head(): A? {
        if (!hdDefined) {
            hd = next()
            hdDefined = true
        }

        return hd
    }

    fun hasNext() = hdDefined || wrappedIt.hasNext()

    fun next() = if (hdDefined) {
        hdDefined = false
        hd
    } else {
        wrappedIt.next()
    }
}


/** Creates a buffered iterator from this iterator. This method is a simple copy from scala's collection API
 *
 *  @return  a buffered iterator producing the same values as this iterator.
 */
fun <A> Iterator<A>.buffered() = BufferedIterator<A>(this)