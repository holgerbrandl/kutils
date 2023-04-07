package kutils

import java.io.File

operator fun File.div(childName: String): File {
    return this.resolve(childName)
}

