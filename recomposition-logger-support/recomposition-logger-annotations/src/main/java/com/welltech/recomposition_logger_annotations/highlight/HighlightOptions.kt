package com.welltech.recomposition_logger_annotations.highlight

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

var debugHighlightOptions by mutableStateOf(HighlightOptions())

data class HighlightOptions(
    val enabled: Boolean = false,
    val durationMillis: Long = 100,
    val normalColor: Color = Color.Green,
    val recompositionColor: Color = Color.Red,
)
