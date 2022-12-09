package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.spec.mapEach
import com.steno.adventofcode.util.math.Vector2
import com.steno.adventofcode.util.math.Vector2.Companion.UNIT_X
import com.steno.adventofcode.util.math.Vector2.Companion.UNIT_Y
import com.steno.adventofcode.util.math.Vector2.Companion.ZERO
import kotlin.math.max
import kotlin.math.min

class Day9 : AdventOfCodeSpec({ challenge ->
    challenge.mapEach { Instruction.parse(it) }
        .eval(13, 6197) { Rope(ZERO, 1).handleInstructions(it).lastVisited.size }
        .eval(1, 2562) { Rope(ZERO, 9).handleInstructions(it).lastVisited.size }
}) {
    data class Rope(
        val head: Vector2,
        val tails: List<Vector2>,
        val lastVisited: Set<Vector2>,
    ) {
        constructor(initial: Vector2, tailCount: Int): this(initial, (1..tailCount).map { initial }, setOf(initial))

        fun handleInstructions(
            instructions: Sequence<Instruction>,
            debug: Boolean = false,
            print: (Rope) -> String = { it.toString(0..5, 4 downTo 0) }
        ) = instructions
            .onEach { if (debug) println(it) }
            .flatMap { it.steps }
            .fold(this) { rope, direction ->
                rope.step(direction)
                    .also { if (debug) println(print(it)) }
            }

        fun step(direction: Direction): Rope {
            val newHead = head + direction.direction
            val newTails = (listOf(newHead) + tails).zipWithNext()
                .map { (prev, tail) -> tail.movedTo(prev) }
            return Rope(newHead, newTails, lastVisited + newTails.last())
        }

        fun toString(rangeX: IntProgression, rangeY: IntProgression) = rangeY.joinToString("\n") { y ->
            rangeX.joinToString("") { x ->
                when (Vector2(x, y)) {
                    head -> "H"
                    tails.last() -> "T"
                    ZERO -> "s"
                    else -> "."
                }
            }
        } + "\n"

        private fun Vector2.movedTo(target: Vector2): Vector2 {
            val distance = target - this
            return when {
                distance.maxNorm <= 1 -> this
                else -> this + distance.clip(-1..1)
            }
        }

        private fun Int.clip(range: IntRange) = max(range.first, min(range.last, this))
        private fun Vector2.clip(range: IntRange) = Vector2(x.clip(range), y.clip(range))
    }

    data class Instruction(val direction: Direction, val count: Int) {
        val steps
            get() = (1..count).asSequence().map { direction }

        override fun toString() = "== $direction $count ==\n"

        companion object {
            fun parse(line: String) = line.split(' ').let { (dir, count) -> Instruction(Direction.valueOf(dir), count.toInt()) }
        }
    }

    enum class Direction(val direction: Vector2) {
        L(-UNIT_X),
        R(UNIT_X),
        U(UNIT_Y),
        D(-UNIT_Y)
    }
}
