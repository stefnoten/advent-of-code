package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.math.Vector2
import com.steno.adventofcode.util.math.Vector2.Companion.UNIT_X
import com.steno.adventofcode.util.math.Vector2.Companion.UNIT_Y
import com.steno.adventofcode.util.repeat
import com.steno.adventofcode.util.untilStable
import kotlin.math.max

class Day17 : AdventOfCodeSpec({ challenge ->
    challenge.map { it.first().toJetStream() }
        .eval(3068) {
                jetStream ->
            val nextRock = rocks().iterator()::next
            val nextJet = jetStream.repeat().iterator()::next
            sequence {
                var state = State()
                while (true) {
                    if (state.fallingRock == null)
                        state = state.withNextRock(nextRock()).also { yield(it) }
                    state = state.applyForce(nextJet()).also { yield(it) }
                    state = state.applyForce(-UNIT_Y).also { yield(it) }
                }
            }
                .filter { it.fallingRock == null }
                .take(2022)
                .last()
                .height
        }
}) {
    class Rock(val positions: List<Vector2>) : Iterable<Vector2> by positions {
        operator fun plus(offset: Vector2) = Rock(positions.map { it + offset })
    }

    data class State(
        val fallenRocks: Set<Vector2> = setOf(),
        val fallingRock: Rock? = null,
        val maxFallenY: Int = -1
    ) {
        val rangeY get() = 0..max(maxFallenY, fallingRock?.maxOf { it.y } ?: -1)
        val height get() = rangeY.last + 1

        fun applyForce(delta: Vector2): State {
            val updatedRock = fallingRock!! + delta
            val moved = updatedRock.none { it in fallenRocks || it.x !in rangeX || it.y < 0 }
            return when {
                moved -> copy(fallingRock = updatedRock)
                delta.y == 0 -> this
                else -> copy(
                    fallenRocks = fallenRocks + fallingRock,
                    fallingRock = null,
                    maxFallenY = rangeY.last
                )
            }
        }

        fun withNextRock(rock: Rock): State = copy(fallingRock = rock + Vector2(2, rangeY.last + 4))

        override fun toString() = rangeY.reversed().joinToString("\n") { y ->
            rangeX.joinToString("", "|", "|") { x ->
                when (Vector2(x, y)) {
                    in fallenRocks -> "#"
                    in fallingRock ?: emptySet() -> "@"
                    else -> "."
                }
            }
        } + rangeX.joinToString("", "\n+", "+\n") { "-" }

        companion object {
            val rangeX = 0..6
        }
    }

    companion object {
        val Minus = Rock(listOf(Vector2(0, 0), Vector2(1, 0), Vector2(2, 0), Vector2(3, 0)))
        val Plus = Rock(listOf(Vector2(1, 0), Vector2(0, 1), Vector2(1, 1), Vector2(2, 1), Vector2(1, 2)))
        val ReverseL = Rock(listOf(Vector2(0, 0), Vector2(1, 0), Vector2(2, 0), Vector2(2, 1), Vector2(2, 2)))
        val Vertical = Rock(listOf(Vector2(0, 0), Vector2(0, 1), Vector2(0, 2), Vector2(0, 3)))
        val Block = Rock(listOf(Vector2(0, 0), Vector2(1, 0), Vector2(0, 1), Vector2(1, 1)))
        fun rocks() = sequenceOf(Minus, Plus, ReverseL, Vertical, Block).repeat()

        private fun String.toJetStream() = map {
            when (it) {
                '>' -> UNIT_X
                '<' -> -UNIT_X
                else -> throw IllegalStateException("Unknown: $it")
            }
        }.asSequence()

    }
}
