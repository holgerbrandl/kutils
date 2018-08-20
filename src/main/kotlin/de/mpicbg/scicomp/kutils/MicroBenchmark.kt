package de.mpicbg.scicomp.kutils

/**
 * @author Holger Brandl
 */

class MicroBenchmark<T> {

    var reps = 25
    var warmupReps = 10

    val results = emptyList<BenchmarkResult<T>>().toMutableList()

    // inspired from https://gist.github.com/maxpert/6ca20fe1a70ccf6ef3a5
    inline fun elapseNano(desc: T? = null, callback: () -> Unit): BenchmarkResult<T> {
        (1..warmupReps).forEach { callback() }

        return (1..reps).map {
            print(".")
            val start = System.nanoTime()
            callback()
            System.nanoTime() - start
        }.let { BenchmarkResult(it, desc) }.also {
            results.add(it)
            println()
        }

    }

    fun printSummary() {
        for (result in results) {
            result.printSummary()
        }
    }
}


internal fun List<Number>.mean(): Double = map { it.toDouble() }.sum() / size

internal fun List<Number>.sd() = if (size == 1) null else Math.sqrt(map { Math.pow(it.toDouble() - mean(), 2.toDouble()) }.sum() / size.toDouble())

data class BenchmarkResult<T>(val times: List<Long>, val config: T?) {
    fun printSummary() {
        println("${config}:\t  mean=${times.mean() / 1E9}\t  sd=${times.sd() ?: kotlin.Double.Companion.NaN/1E9}")
    }
}

fun main(args: Array<String>) {


    val mb = MicroBenchmark<String>()

    // set number of repetitions
    mb.reps = 10


    // run config a
    mb.elapseNano("config a") {
        2 + 2
    }

    mb.elapseNano("config b") {
        2 + 2
    }

    mb.printSummary()


}