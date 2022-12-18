package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.spec.mapEach
import com.steno.adventofcode.util.math.Vector3
import com.steno.adventofcode.util.math.Vector3.Companion.UNIT_X
import com.steno.adventofcode.util.math.Vector3.Companion.UNIT_Y
import com.steno.adventofcode.util.math.Vector3.Companion.UNIT_Z
import com.steno.adventofcode.util.math.max
import com.steno.adventofcode.util.math.min
import com.steno.adventofcode.util.pathfinding.Edge
import com.steno.adventofcode.util.pathfinding.dijkstraShortestPathFrom

class Day18 : AdventOfCodeSpec({ challenge ->
    challenge
        .mapEach { it.split(",").let { (x, y, z) -> Vector3(x.toInt(), y.toInt(), z.toInt()) } }
        .map { Cube(it.toSet()) }
        .eval(64, 76, 36, 4444) { it.surfaceArea }
        .eval(58, 66, 30, 2530) { it.exteriorSurfaceArea }
}) {

    data class Cube(val points: Set<Vector3>) {
        private val surface: Sequence<Vector3>
            get() = points.asSequence().flatMap { it.neighbours() } - points

        val surfaceArea: Int
            get() = surface.count()

        private fun exteriorSurface(): Sequence<Vector3> {
            val fullExteriorSpace = (points.min() - Vector3.ONE)..(points.max() + Vector3.ONE)
            val emptySpace = (fullExteriorSpace - points).toSet()
            val paths = dijkstraShortestPathFrom(fullExteriorSpace.min, emptySpace.asSequence()) { from ->
                from.neighbours()
                    .filter { it in emptySpace }
                    .map { Edge(it, 1) }
            }
            return surface.filter { paths.canReach(it) }
        }

        val exteriorSurfaceArea: Int
            get() = exteriorSurface().count()


        private fun Vector3.neighbours() = sequenceOf(-UNIT_X, UNIT_X, -UNIT_Y, UNIT_Y, -UNIT_Z, UNIT_Z).map { this + it }
    }
}
