package com.steno.adventofcode.util.math

data class Vector2Range(
    val x: IntRange,
    val y: IntRange,
): VectorRange<Vector2> {
    override val min: Vector2
        get() = Vector2(x.first, y.first)
    override val max: Vector2
        get() = Vector2(x.last, y.last)

    override fun contains(vector: Vector2) = vector.x in x && vector.y in y

    override fun iterator() = x.asSequence().flatMap { x ->
        y.asSequence().map { y ->
            Vector2(x, y)
        }
    }.iterator()

    override fun toString() = "($x,$y)"
}
