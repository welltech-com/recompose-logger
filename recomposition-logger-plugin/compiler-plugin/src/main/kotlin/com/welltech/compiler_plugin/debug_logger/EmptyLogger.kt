package com.welltech.compiler_plugin.debug_logger

class EmptyLogger : Logger {
    override fun logMsg(msg: String) {
        // do nothing
    }
}
