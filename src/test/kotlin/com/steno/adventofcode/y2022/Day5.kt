package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.inOrder
import com.steno.adventofcode.util.parse
import com.steno.adventofcode.util.takeUntil

class Day5 : AdventOfCodeSpec({ challenge ->
    challenge.map { lines ->
        lines.inOrder(
            { l -> l.takeUntil { it.isEmpty() }.let { Stacks.parse(it) } },
            { Move.parse(it) }
        )
    }.eval("CMZ", "SPFMVDTZT") { (initialStacks, moves) ->
        moves
            .fold(initialStacks) { stacks, move -> stacks.movePerOne(move) }
            .topBoxes
    }.eval("MCD", "ZFSJBPRFP") { (initialStacks, moves) ->
        moves
            .fold(initialStacks) { stacks, move -> stacks.moveAtOnce(move) }
            .topBoxes
    }
}) {

    data class Stacks(val stacks: List<String>) {
        val topBoxes = stacks.joinToString("") { it.take(1) }

        private fun take(count: Int, from: Stack) = stacks[from.index].take(count) to Stacks(
            stacks.mapIndexed { i, stack ->
                when (i) {
                    from.index -> stack.drop(count)
                    else -> stack
                }
            }
        )

        private fun add(boxes: String, to: Stack) = Stacks(
            stacks.mapIndexed { i, stack ->
                when (i) {
                    to.index -> boxes + stack
                    else -> stack
                }
            }
        )

        fun movePerOne(move: Move) = move.let { (_, from, to) ->
            val (boxes, result) = take(move.count, from = from)
            result.add(boxes.reversed(), to)
        }

        fun moveAtOnce(move: Move) = move.let { (_, from, to) ->
            val (boxes, result) = take(move.count, from = from)
            result.add(boxes, to)
        }

        override fun toString() = stacks.joinToString(", ", "Stacks([", "])")

        companion object {
            fun parse(lines: Sequence<String>) = lines.toList()
                .dropLast(1)
                .flatMap { line ->
                    line.windowed(4, 4, true) { it[1] }
                        .withIndex()
                        .filter { (_, box) -> box.isLetter() }
                }
                .groupBy({ it.index }, { it.value })
                .toSortedMap()
                .values
                .map { it.joinToString("") }
                .let { Stacks(it) }
        }
    }

    @JvmInline
    value class Stack(val number: Int) {
        val index get() = number - 1

        override fun toString() = "$number"
    }

    data class Move(val count: Int, val from: Stack, val to: Stack) {
        override fun toString() = "Move($count from $from to $to)"

        companion object {
            private val LINE_FORMAT = Regex("""move (\d+) from (\d+) to (\d+)""")

            fun parse(lines: Sequence<String>) = lines.map {
                LINE_FORMAT.parse(it) { (count, from, to) ->
                    Move(count.toInt(), Stack(from.toInt()), Stack(to.toInt()))
                }
            }
        }
    }
}
