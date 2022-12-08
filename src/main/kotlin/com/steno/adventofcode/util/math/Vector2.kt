package com.steno.adventofcode.util.math

import kotlin.math.abs

data class Vector2(val x: Int, val y: Int) {

    val norm1 get() = abs(x) + abs(y)

    operator fun unaryMinus() = Vector2(-x, -y)
    operator fun plus(other: Vector2) = Vector2(x + other.x, y + other.y)
    operator fun minus(other: Vector2) = Vector2(x - other.x, y - other.y)
    override fun toString() = "($x,$y)"

    companion object {
        val ZERO = Vector2(0, 0)
        val UNIT_X = Vector2(1, 0)
        val UNIT_Y = Vector2(0, 1)
    }
}
