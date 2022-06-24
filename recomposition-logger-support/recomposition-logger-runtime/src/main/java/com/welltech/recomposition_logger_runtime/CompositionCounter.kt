package com.welltech.recomposition_logger_runtime

/**
 * Wrapper over the Int value to prevent recomposition after changing [value]
 *
 * @property value current amount of recomposition
 */
@PublishedApi
internal class CompositionCounter(var value: Int)