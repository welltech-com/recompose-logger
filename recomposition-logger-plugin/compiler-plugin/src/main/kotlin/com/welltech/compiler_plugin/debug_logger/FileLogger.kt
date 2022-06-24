package com.welltech.compiler_plugin.debug_logger

import java.io.File
import java.io.FileOutputStream

class FileLogger(
    logFile: File
): Logger {

    private val file = if (logFile.isDirectory) {
        File(logFile, "recomposition-compiler-logs.txt")
    } else {
        logFile
    }

    private val writer by lazy {
        FileOutputStream(file, true)
    }

    init {
        file.delete()
        file.createNewFile()
    }


    override fun logMsg(msg: String) {
        writer.write(msg.toByteArray())
        writer.write("\n".toByteArray())
    }
}