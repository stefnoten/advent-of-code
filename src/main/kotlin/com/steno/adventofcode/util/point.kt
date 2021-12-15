package com.steno.adventofcode.util

data class Vector(val x: Int, val y: Int) {

    operator fun unaryMinus() = Vector(-x, -y)
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
    operator fun minus(other: Vector) = Vector(x - other.x, y - other.y)
    override fun toString() = "($x,$y)"

    companion object {
        val ZERO = Vector(0, 0)
        val UNIT_X = Vector(1, 0)
        val UNIT_Y = Vector(0, 1)
    }
}

fun Vector.toPair() = x to y
