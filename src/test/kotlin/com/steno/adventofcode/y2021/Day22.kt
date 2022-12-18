package com.steno.adventofcode.y2021

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.intersect
import com.steno.adventofcode.util.parse

private class Day22 : AdventOfCodeSpec({ challenge ->
    val subset = IntRange3(-50..50, -50..50, -50..50)

    challenge.map { parse(it) }
        .eval(39, 590784, 474140, 567496) { instructions ->
            val space = instructions
                .fold(Space()) { acc, (on, range) ->
                    when (on) {
                        true -> acc + range
                        false -> acc - range
                    }
                }
                .intersect(subset)
            space.volume
        }
        .eval(39, 39769202357779, 2758514936282235, 1355961721298916) { instructions ->
            val space = instructions
                .fold(Space()) { acc, (on, range) ->
                    when (on) {
                        true -> acc + range
                        false -> acc - range
                    }
                }
            space.volume
        }
}) {
    data class Instruction(val on: Boolean, val range: IntRange3)
    data class IntRange3(val x: IntRange, val y: IntRange, val z: IntRange) {
        fun isEmpty() = x.isEmpty() || y.isEmpty() || z.isEmpty()
        val volume get() = x.size.toLong() * y.size.toLong() * z.size.toLong()

        fun intersect(other: IntRange3): IntRange3? = IntRange3(
            x intersect other.x,
            y intersect other.y,
            z intersect other.z,
        ).takeIf { !it.isEmpty() }

        operator fun minus(other: IntRange3): List<IntRange3> = when (val overlap = this.intersect(other)) {
            null -> listOf(this)
            else -> {
                val beforeX = x.first until overlap.x.first
                val afterX = (overlap.x.last + 1)..x.last
                val beforeY = y.first until overlap.y.first
                val afterY = (overlap.y.last + 1)..y.last
                val beforeZ = z.first until overlap.z.first
                val afterZ = (overlap.z.last + 1)..z.last
                sequenceOf(
                    IntRange3(beforeX, y, z),
                    IntRange3(overlap.x, beforeY, z),
                    IntRange3(overlap.x, overlap.y, beforeZ),
                    IntRange3(overlap.x, overlap.y, afterZ),
                    IntRange3(overlap.x, afterY, z),
                    IntRange3(afterX, y, z),
                ).filterNot { it.isEmpty() }.toList()
            }
        }

        private val IntRange.size get() = maxOf(0, last - first + 1)

        override fun toString() = "{$x,$y,$z}"
    }

    class Space private constructor(val disjointRanges: List<IntRange3>) {
        constructor() : this(emptyList())

        val volume get() = disjointRanges.sumOf { it.volume }

        infix fun intersect(range: IntRange3) = Space(disjointRanges.mapNotNull { it.intersect(range) })
        operator fun plus(range: IntRange3) = Space(disjointRanges.flatMap { it - range } + range)
        operator fun minus(range: IntRange3) = Space(disjointRanges.flatMap { it - range })

        override fun toString() = if (disjointRanges.isEmpty()) "{}" else disjointRanges.joinToString(" ∪ ")
    }

    companion object {
        val PATTERN = Regex("""(on|off) x=(-?\d+)\.\.(-?\d+),y=(-?\d+)\.\.(-?\d+),z=(-?\d+)\.\.(-?\d+)""")

        fun parse(lines: Sequence<String>) = lines.map {
            PATTERN.parse(it) { (onOff, x1, x2, y1, y2, z1, z2) ->
                Instruction(
                    on = onOff == "on",
                    range = IntRange3(
                        x1.toInt()..x2.toInt(),
                        y1.toInt()..y2.toInt(),
                        z1.toInt()..z2.toInt()
                    )
                )
            }
        }
    }
}
