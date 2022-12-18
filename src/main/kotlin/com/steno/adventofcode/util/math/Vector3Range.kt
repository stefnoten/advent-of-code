package com.steno.adventofcode.util.math

data class Vector3Range(
    val x: IntRange,
    val y: IntRange,
    val z: IntRange,
) : VectorRange<Vector3> {
    override val min: Vector3
        get() = Vector3(x.first, y.first, z.first)
    override val max: Vector3
        get() = Vector3(x.last, y.last, z.last)

    override fun contains(vector: Vector3) = vector.x in x && vector.y in y && vector.z in z

    override fun iterator() = x.asSequence().flatMap { x ->
        y.asSequence().flatMap { y ->
            z.asSequence().map { z ->
                Vector3(x, y, z)
            }
        }
    }.iterator()

    override fun toString() = "($x,$y,$z)"
}
