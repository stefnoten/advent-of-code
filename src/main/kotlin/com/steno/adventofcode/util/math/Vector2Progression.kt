package com.steno.adventofcode.util.math

import com.steno.adventofcode.util.totalSteps

data class Vector2Progression(
    override val first: Vector2,
    override val step: Vector2,
    override val totalSteps: Int,
): VectorProgression<Vector2>, Iterable<Vector2> {
    constructor(x: IntProgression, y: Int): this(Vector2(x.first, y), Vector2(x.step, 0), x.totalSteps)
    constructor(x: Int, y: IntProgression): this(Vector2(x, y.first), Vector2(0, y.step), y.totalSteps)

    override fun toString() = "$first..$last step $step"
}
