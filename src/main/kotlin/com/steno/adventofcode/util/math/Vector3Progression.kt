package com.steno.adventofcode.util.math

import com.steno.adventofcode.util.totalSteps

data class Vector3Progression(
    override val first: Vector3,
    override val step: Vector3,
    override val totalSteps: Int,
): VectorProgression<Vector3>, Iterable<Vector3> {
    constructor(x: IntProgression, y: Int, z: Int): this(Vector3(x.first, y, z), Vector3(x.step, 0, 0), x.totalSteps)
    constructor(x: Int, y: IntProgression, z: Int): this(Vector3(x, y.first, z), Vector3(0, y.step, 0), y.totalSteps)
    constructor(x: Int, y: Int, z: IntProgression): this(Vector3(x, y, z.first), Vector3(0, 0, z.step), z.totalSteps)
}
