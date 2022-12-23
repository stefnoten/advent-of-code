package com.steno.adventofcode.util.math

import kotlin.math.abs
import kotlin.math.max

data class Vector2(val x: Int, val y: Int): Vector<Vector2> {

    val norm1 get() = abs(x) + abs(y)
    val maxNorm get() = max(abs(x), abs(y))

    override fun unaryMinus() = Vector2(-x, -y)
    override fun plus(other: Vector2) = Vector2(x + other.x, y + other.y)
    override fun minus(other: Vector2) = Vector2(x - other.x, y - other.y)
    override fun times(factor: Int) = Vector2(factor * x, factor * y)
    override fun div(factor: Int) = Vector2(x / factor, y / factor)

    override fun rangeTo(other: Vector2) = Vector2Range(x..other.x, y..other.y)

    override fun stepBy(step: Vector2, totalSteps: Int) = Vector2Progression(this, step, totalSteps)
    override fun dot(other: Vector2) = x * x + y * y

    override fun toString() = "($x,$y)"

    companion object {
        val ZERO = Vector2(0, 0)
        val ONE = Vector2(1, 1)
        val UNIT_X = Vector2(1, 0)
        val UNIT_Y = Vector2(0, 1)
    }
}

fun Iterable<Vector2>.min() = Vector2(minOf { it.x }, minOf { it.y })
fun Iterable<Vector2>.max() = Vector2(maxOf { it.x }, maxOf { it.y })
fun Sequence<Vector2>.min() = Vector2(minOf { it.x }, minOf { it.y })
fun Sequence<Vector2>.max() = Vector2(maxOf { it.x }, maxOf { it.y })
