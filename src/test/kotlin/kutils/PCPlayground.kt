package kutils

import java.text.DecimalFormat

fun main(args: Array<String>) {

    time {
        val result = (1..10000000).pmap { listOf(Math.sin(it.toDouble())) }
        print("test:" + result.size)
    }


}

fun time(msg: String = "runtime was ", body: () -> Any) {
    val s = System.nanoTime()
    body()
    print("\n" + msg + ": " +  DecimalFormat("#0.##").format((System.nanoTime() - s) / 1e9) + "sec")
}