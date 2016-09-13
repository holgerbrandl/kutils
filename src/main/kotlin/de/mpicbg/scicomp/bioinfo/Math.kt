package de.mpicbg.scicomp.bioinfo


fun Array<Double>.mean() = this.sum() / this.size

// http://stackoverflow.com/questions/4662292/scala-median-implementation
fun Array<Double>.median() = {
    val (lower, upper) = this.sorted().let { Pair(it.drop(it.size / 2), it.take(it.size / 2)) }

    if (this.size % 2 == 0) (lower.last() + upper.first()) / 2.0 else upper.first()
}

//http://stackoverflow.com/questions/23086291/format-in-kotlin-string-templates
fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

