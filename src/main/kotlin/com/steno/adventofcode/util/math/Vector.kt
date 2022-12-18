package com.steno.adventofcode.util.math

interface Vector<T: Vector<T>> {
    operator fun unaryMinus(): T
    operator fun plus(other: T): T
    operator fun minus(other: T): T
    operator fun times(factor: Int): T
    fun stepBy(step: T, totalSteps: Int): VectorProgression<T>
    infix fun dot(other: T): Int
}
