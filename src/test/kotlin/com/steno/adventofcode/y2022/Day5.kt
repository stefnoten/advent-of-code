package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.inOrder
import com.steno.adventofcode.util.parse
import com.steno.adventofcode.util.takeUntil

class Day5 : AdventOfCodeSpec({
    parseAllLines { lines ->
        lines.inOrder(
            { l -> l.takeUntil { it.isEmpty() }.let { Stacks.parse(it) } },
            { Move.parse(it) }
        )
    }.eval { (initialStacks, moves) ->
        moves
            .fold(initialStacks) { stacks, move -> stacks.movePerOne(move) }
            .topBoxes
    }.eval { (initialStacks, moves) ->
        moves
            .fold(initialStacks) { stacks, move -> stacks.moveAtOnce(move) }
            .topBoxes
    }
}) {

    data class Stacks(val stacks: Map<Int, String>) {
        val topBoxes = stacks.toSortedMap().values.joinToString("") { it.take(1) }

        private fun take(count: Int, from: Int) = stacks[from]!!.take(count) to Stacks(
            stacks - from + (from to stacks[from]!!.drop(count))
        )

        private fun add(boxes: String, dest: Int) = Stacks(
            stacks - dest + (dest to boxes + stacks[dest]!!)
        )

        fun movePerOne(move: Move) = move.let { (_, src, dest) ->
            val (boxes, result) = take(move.count, from = src)
            result.add(boxes.reversed(), dest)
        }

        fun moveAtOnce(move: Move) = move.let { (_, src, dest) ->
            val (boxes, result) = take(move.count, from = src)
            result.add(boxes, dest)
        }

        override fun toString() = stacks.toSortedMap().values.joinToString(", ", "Stacks([", "])")

        companion object {
            fun parse(lines: Sequence<String>) = lines.toList()
                .dropLast(1)
                .flatMap { line ->
                    line.windowed(4, 4, true) { it[1] }
                        .mapIndexed { i, box -> i + 1 to box }
                        .filter { (_, box) -> box.isLetter() }
                }
                .groupBy({ it.first }, { it.second })
                .toSortedMap()
                .mapValues { (_, boxes) -> boxes.joinToString("") }
                .let { Stacks(it) }
        }
    }

    data class Move(val count: Int, val from: Int, val to: Int) {
        override fun toString() = "Move($count from $from to $to)"

        companion object {
            private val LINE_FORMAT = Regex("""move (\d+) from (\d+) to (\d+)""")

            fun parse(lines: Sequence<String>) = lines.map {
                LINE_FORMAT.parse(it) { (count, from, to) ->
                    Move(count.toInt(), from.toInt(), to.toInt())
                }
            }
        }
    }
}
