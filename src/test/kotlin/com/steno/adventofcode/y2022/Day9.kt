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
//        .focusOn("example")
        .eval(13) { Rope().handle(it).tailVisited.size }
}) {
    data class Rope(
        val head: Vector2 = ZERO,
        val tail: Vector2 = ZERO,
        val tailVisited: Set<Vector2> = setOf(ZERO),
    ) {

        fun handle(instructions: Sequence<Instruction>) = instructions
//            .onEach { println("== ${it.direction} ${it.count} ==\n") }
            .flatMap { it.steps }
            .fold(this) { rope, direction ->
                rope.step(direction)
//                    .also { println(it.toString(0..5, 4 downTo 0)) }
            }

        fun step(direction: Direction): Rope {
            val newHead = head + direction.direction
            val newTail = movedTail(newHead)
            return Rope(newHead, newTail, tailVisited + newTail)
        }

        fun toString(rangeX: IntProgression, rangeY: IntProgression) = rangeY.joinToString("\n") { y ->
            rangeX.joinToString("") { x ->
                when (Vector2(x, y)) {
                    head -> "H"
                    tail -> "T"
                    ZERO -> "s"
                    else -> "."
                }
            }
        } + "\n"

        private fun movedTail(head: Vector2): Vector2 {
            val distance = head - tail
            return when {
                distance.normMax <= 1 -> tail
                else -> tail + distance.clip(-1..1)
            }
        }

        private fun Int.clip(range: IntRange) = max(range.first, min(range.last, this))
        private fun Vector2.clip(range: IntRange) = Vector2(x.clip(range), y.clip(range))
    }

    data class Instruction(val direction: Direction, val count: Int) {
        val steps
            get() = (1..count).asSequence().map { direction }

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
