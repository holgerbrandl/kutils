package de.mpicbg.scicomp.kutils

import joblist.*
import java.io.File

//import scala.collection.JavaConversions.*

/**
 * Created by brandl on 10/28/16.
 */

class JoblistSupportAPI {

    companion object {
        fun JobList(): JobList = JobList(".")

        fun JobList.createHtmlReport() {
            joblist.`package$`.`MODULE$`.ImplJobListUtils(this).createHtmlReport()
        }

        fun JobList.createHtmlReport(resubStrategy: ResubmitStrategy = TryAgain(),
                                     configRoots: scala.collection.immutable.List<Job> = joblist.`package$`.`MODULE$`.getConfigRoots(this.requiresRerun())) {
            return resubmit(resubStrategy, configRoots)
        }


        fun JobConfig(cmd: String, name: String = "", wallTime: String = "", queue: String = "", numThreads: Int = 1, maxMemory: Int = 0, otherQueueArgs: String = "", wd: File): JobConfiguration {
            return JobConfiguration(cmd, name, wallTime, queue, numThreads, maxMemory, otherQueueArgs, better.files.File(wd.toPath()))
        }
    }
}