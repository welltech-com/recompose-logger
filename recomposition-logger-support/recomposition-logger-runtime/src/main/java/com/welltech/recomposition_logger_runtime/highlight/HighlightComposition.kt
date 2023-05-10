package com.welltech.recomposition_logger_runtime.highlight

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.welltech.recomposition_logger_annotations.highlight.debugHighlightOptions
import com.welltech.recomposition_logger_runtime.CompositionCounter
import kotlinx.coroutines.delay

/**
 * Draw rect around composable to which this modifier is applied.
 * Rect color depended on count of recomposition: [debugHighlightOptions.normalColor] - recomposition count is 0, else - [debugHighlightOptions.recompositionColor].
 * Count of recomposition are dropped after [debugHighlightOptions.durationMillis] ms if during this time no recomposition has occurred
 *
 * This method designed for using with recomposition-plugin and does not involve manual use
 */
fun Modifier.highlightRecomposition(funName: String) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "recomposeHighlighter"
        properties["funName"] = funName
    },
) {
    if (debugHighlightOptions.enabled.not()) {
        return@composed this
    }
    val duration = debugHighlightOptions.durationMillis

    val totalCompositions = remember { CompositionCounter(-1) }
    totalCompositions.value++

    val totalCompositionsAtLastTimeout = remember { mutableStateOf(0) }

    LaunchedEffect(totalCompositions.value) {
        delay(duration)
        totalCompositionsAtLastTimeout.value = totalCompositions.value
    }

    drawWithCache {
        val paint = Paint().apply {
            this.color = Color.White
            this.isAntiAlias = true
        }.asFrameworkPaint()
        paint.textSize = 8.sp.toPx()

        val textWidth = paint.measureText(funName)

        onDrawWithContent {
            drawContent()

            val numCompositionsSinceTimeout =
                totalCompositions.value - totalCompositionsAtLastTimeout.value

            val hasValidBorderParams = size.minDimension > 0f
            if (!hasValidBorderParams) {
                return@onDrawWithContent
            }

            val color = if (numCompositionsSinceTimeout > 0) {
                debugHighlightOptions.recompositionColor
            } else {
                debugHighlightOptions.normalColor
            }

            drawRect(color = color, style = Stroke(1.dp.toPx()))
            drawRect(
                color = Color.Black,
                topLeft = Offset(size.width - textWidth, 0f),
                size = Size(textWidth, 10.sp.toPx()),
                alpha = 0.5f,
            )
            drawContext.canvas.nativeCanvas.drawText(funName, size.width - textWidth, 20f, paint)
        }
    }
}
