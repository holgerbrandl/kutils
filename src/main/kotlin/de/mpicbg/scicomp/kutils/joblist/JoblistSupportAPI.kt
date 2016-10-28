package de.mpicbg.scicomp.kutils.joblist

import joblist.*
import scala.collection.immutable.List
import java.io.File

//import scala.collection.JavaConversions.*

// http://stackoverflow.com/questions/33717490/kotlin-cannot-import-on-demand-from-object

fun JobList(): JobList = JobList(".")

fun JobList.createHtmlReport() {
    `package$`.`MODULE$`.ImplJobListUtils(this).createHtmlReport()
}

fun JobList.createHtmlReport(resubStrategy: ResubmitStrategy = TryAgain(),
                             configRoots: List<Job> = `package$`.`MODULE$`.getConfigRoots(this.requiresRerun())) {
    return resubmit(resubStrategy, configRoots)
}


fun JobConfig(cmd: String, name: String = "", wallTime: String = "", queue: String = "", numThreads: Int = 1, maxMemory: Int = 0, otherQueueArgs: String = "", wd: File = File(".")): JobConfiguration {
    return JobConfiguration(cmd, name, wallTime, queue, numThreads, maxMemory, otherQueueArgs, better.files.File(wd.toPath()))
}
