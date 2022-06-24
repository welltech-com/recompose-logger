package com.welltech.recomposition_logger_runtime

import android.util.Log
import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * An effect which logs the number compositions at the invoked point of the slot table.
 * Reference source [chrisbanes/tivi](https://github.com/chrisbanes/tivi/blob/main/common-ui-compose/src/main/java/app/tivi/common/compose/Debug.kt)
 *
 * This is an inline function to act as like a C-style #include to the host composable function.
 * That way we track it's compositions, not this function's compositions.
 *
 * Log example:
 *
 * RecompositionLogs: #"WorkoutItem id: 15" recomposed 1 times
 *
 * RecompositionLogs: #"WorkoutItem id: 15" recomposed 2 times
 *
 * RecompositionLogs: #"WorkoutItem id: 15" recomposed 3 times
 *
 * @param name name of the composition to identify it in the logcat.
 * @param tag Log tag used for [Log.i]
 */
@Composable
@NonRestartableComposable
fun LogComposition(
    name: String,
    tag: String,
) {
    val scope = rememberCoroutineScope(getContext = { Dispatchers.Default })
    val counter = remember { CompositionCounter(0) }
    SideEffect {
        val amount = ++counter.value
        if (amount > 1) {
            scope.launch {
                Log.i(tag, "#\"$name\" recomposed $amount times")
            }
        }
    }
}