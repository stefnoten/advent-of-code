package com.steno.adventofcode.util

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

data class Vector3(val x: Int, val y: Int, val z: Int) {

    operator fun unaryMinus() = Vector3(-x, -y, -z)
    operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
    override fun toString() = "($x,$y,$z)"

    infix fun dot(other: Vector3) = x * other.x + y * other.y + z * other.z
    infix fun cross(other: Vector3) = Vector3(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )

    companion object {
        val ZERO = Vector3(0, 0, 0)
        val UNIT_X = Vector3(1, 0, 0)
        val UNIT_Y = Vector3(0, 1, 0)
        val UNIT_Z = Vector3(0, 0, 1)
    }
}

data class Matrix3(
    val a11: Int, val a12: Int, val a13: Int,
    val a21: Int, val a22: Int, val a23: Int,
    val a31: Int, val a32: Int, val a33: Int,
) {
    val row1 = Vector3(a11, a12, a13)
    val row2 = Vector3(a21, a22, a23)
    val row3 = Vector3(a31, a32, a33)
    val column1 = Vector3(a11, a21, a31)
    val column2 = Vector3(a12, a22, a32)
    val column3 = Vector3(a13, a23, a33)
    val basis get() = columns
    val rows get() = Triple(row1, row2, row3)
    val columns get() = Triple(column1, column2, column3)

    operator fun times(vector: Vector3) = Vector3(
        row1 dot vector,
        row2 dot vector,
        row3 dot vector,
    )

    operator fun times(other: Matrix3) = Matrix3(
        row1 dot other.column1, row1 dot other.column2, row1 dot other.column3,
        row2 dot other.column1, row2 dot other.column2, row2 dot other.column3,
        row3 dot other.column1, row3 dot other.column2, row3 dot other.column3,
    )

    companion object {
        val IDENTITY = basis(Vector3.UNIT_X, Vector3.UNIT_Y, Vector3.UNIT_Z)

        fun basis(base1: Vector3, base2: Vector3, base3: Vector3) = Matrix3(
            base1.x, base2.x, base3.x,
            base1.y, base2.y, base3.y,
            base1.z, base2.z, base3.z,
        )
    }
}
