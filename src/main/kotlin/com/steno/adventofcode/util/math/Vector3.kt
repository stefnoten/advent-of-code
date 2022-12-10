package com.steno.adventofcode.util.math

data class Vector3(val x: Int, val y: Int, val z: Int): Vector<Vector3> {

    override fun unaryMinus() = Vector3(-x, -y, -z)
    override fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)
    override fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
    override fun times(factor: Int) = Vector3(factor * x, factor * y, factor * z)
    override fun stepBy(step: Vector3, totalSteps: Int) = Vector3Range(this, step, totalSteps)
    override fun dot(other: Vector3) = x * other.x + y * other.y + z * other.z
    infix fun cross(other: Vector3) = Vector3(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )
    override fun toString() = "($x,$y,$z)"

    companion object {
        val ZERO = Vector3(0, 0, 0)
        val UNIT_X = Vector3(1, 0, 0)
        val UNIT_Y = Vector3(0, 1, 0)
        val UNIT_Z = Vector3(0, 0, 1)
    }
}