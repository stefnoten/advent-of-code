package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.AnsiColor.YELLOW
import com.steno.adventofcode.util.color
import com.steno.adventofcode.util.math.Vector2
import com.steno.adventofcode.util.math.Vector2.Companion.UNIT_X
import com.steno.adventofcode.util.math.Vector2.Companion.UNIT_Y
import com.steno.adventofcode.util.math.Vector2Range
import com.steno.adventofcode.util.math.max
import com.steno.adventofcode.util.math.min
import com.steno.adventofcode.util.repeat
import com.steno.adventofcode.util.untilStable

class Day23 : AdventOfCodeSpec({ challenge ->
    challenge.map { it.toState() }
        .eval(110, 25, 4158) { initial ->
            Direction.cycle().repeat().runningFold(initial, State::round)
                .elementAt(10)
                .emptyGroundInBounds
        }
        .eval(20, 4, 1014) { initial ->
            Direction.cycle().repeat().runningFold(initial, State::round)
                .drop(1)
                .untilStable()
                .count()
        }
}) {
    data class State(val elves: Set<Vector2>) {
        private val bounds: Vector2Range
            get() = elves.min()..elves.max()
        private val areaInBounds: Int
            get() = bounds.let { it.max - it.min }.let { (it.x + 1) * (it.y + 1) }

        val emptyGroundInBounds: Int
            get() = areaInBounds - elves.size

        fun round(preferredDirection: Direction): State = candidateMoves(preferredDirection).moveIfNoDuplicates()

        private fun candidateMoves(preferredDirection: Direction): Map<Vector2, Vector2> = elves.associateWith { elf ->
            elf.takeIf {
                elf.noneIn(
                    Vector2(-1, -1),
                    Vector2(0, -1),
                    Vector2(1, -1),
                    Vector2(-1, 0),
                    Vector2(1, 0),
                    Vector2(-1, 1),
                    Vector2(0, 1),
                    Vector2(1, 1),
                )
            }
                ?: (Direction.cycleFrom(preferredDirection)
                    .firstOrNull { elf.noneIn(it.value, it.diagonal1, it.diagonal2) }
                    ?.let { elf + it.value })
                ?: elf
        }

        fun Vector2.noneIn(vararg directions: Vector2) = directions.none { this + it in elves }

        private fun Map<Vector2, Vector2>.moveIfNoDuplicates(): State {
            val unique = isUniqueDestination()
            return State(
                elves
                    .map { elf -> this[elf]!!.takeIf(unique) ?: elf }
                    .toSet()
            )
        }

        private fun Map<Vector2, Vector2>.isUniqueDestination(): (Vector2) -> Boolean {
            val countPerDestination = values.groupingBy { it }.eachCount()
            return { countPerDestination[it]!! == 1 }
        }

        override fun toString() = bounds.y.joinToString("\n") { y ->
            bounds.x.joinToString("") { x ->
                if (Vector2(x, y) in elves) "#".color(YELLOW) else "."
            }
        } + "\n"
    }

    enum class Direction(val value: Vector2, orthogonal: Vector2) {
        N(-UNIT_Y, UNIT_X),
        S(UNIT_Y, UNIT_X),
        W(-UNIT_X, UNIT_Y),
        E(UNIT_X, UNIT_Y);

        val diagonal1 = value + orthogonal
        val diagonal2 = value - orthogonal

        companion object {
            fun cycle() = values().asSequence()

            fun cycleFrom(first: Direction) = cycle()
                .repeat()
                .dropWhile { it != first }
                .take(4)
        }
    }

    companion object {
        fun Sequence<String>.toState() = flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, c ->
                Vector2(x, y).takeIf { c == '#' }
            }
        }.let { State(it.toSet()) }
    }
}
