package com.steno.adventofcode.util.math

interface VectorRange<T: Vector<T>>: Iterable<T> {
    val first: T
    val step: T
    val totalSteps: Int
    val last get() = first + step * totalSteps

    override fun iterator() = generateSequence(first) { it + step }.take(totalSteps).iterator()
}
