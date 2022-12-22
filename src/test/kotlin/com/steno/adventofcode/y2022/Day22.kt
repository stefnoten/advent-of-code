package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.inOrder
import com.steno.adventofcode.util.math.Vector2
import com.steno.adventofcode.util.math.Vector2.Companion.UNIT_X
import com.steno.adventofcode.util.math.Vector2.Companion.UNIT_Y
import com.steno.adventofcode.util.split
import com.steno.adventofcode.y2022.Day22.Direction.*
import java.util.StringTokenizer

class Day22 : AdventOfCodeSpec({ challenge ->
    challenge.map { lines ->
        lines.split { it.isEmpty() }.inOrder {
            next().toBoard() to next().first().toInstructions()
        }
    }
        .eval(6032, 191010) { (board, instructions) ->
            instructions.runningFold(Position(at = board.topLeft, direction = RIGHT)) { position, instruction ->
                position.handle(instruction, board)
            }
                .toList().also { positions ->
                    val positionsByAt = positions.associateBy { it.at }
                    println()
                    println(board.toString { pos, c -> positionsByAt[pos]?.direction?.toString()?.first() ?: c })
                }
                .last().password
        }
}) {
    data class Row(val rangeX: IntRange, val wallsAtX: Set<Int>) {
        val first get() = rangeX.first

        operator fun contains(x: Int) = x in rangeX

        fun isWall(x: Int) = x in wallsAtX

        fun toString(render: (x: Int, boardState: Char) -> Char) = " ".repeat(rangeX.first) + rangeX.joinToString("") { x ->
            render(x, if (isWall(x)) '#' else '.').toString()
        }
    }

    data class Board(val rows: List<Row>) {
        val topLeft get() = Vector2(rows.first().first, 0)

        operator fun contains(position: Vector2) = position.y in rows.indices && position.x in rows[position.y]

        fun isWall(position: Vector2) = rows[position.y].isWall(position.x)

        fun step(start: Vector2, direction: Vector2): Vector2? {
            val dest = start + direction
            val wrappedDest = if (dest in this) dest else last(start, -direction)
            return wrappedDest.takeUnless { isWall(it) }
        }

        fun toString(render: (position: Vector2, boardState: Char) -> Char) = rows.withIndex().joinToString("\n") { (y, row) ->
            row.toString { x, state -> render(Vector2(x, y), state) }
        }

        private fun last(start: Vector2, direction: Vector2): Vector2 = when {
            direction.x > 0 -> start.copy(x = rows[start.y].rangeX.last)
            direction.x < 0 -> start.copy(x = rows[start.y].rangeX.first)
            else -> generateSequence(start) { current ->
                (current + direction).takeIf { it in this }
            }.last()
        }
    }

    data class Position(val at: Vector2, val direction: Direction) {
        val password get() = 1000 * (at.y + 1) + 4 * (at.x + 1) + direction.ordinal

        fun handle(instruction: Instruction, board: Board) = when (instruction) {
            is Turn -> turn(instruction.left)
            is Steps -> step(instruction.count, board)
        }

        fun turn(left: Boolean) = copy(direction = direction.turn(left))
        fun step(count: Int, board: Board) = copy(
            at = generateSequence(at) {
                board.step(it, direction.value)
            }.take(count + 1).last()
        )
    }

    data class Instructions(val instructions: List<Instruction>) : Iterable<Instruction> by instructions
    sealed interface Instruction
    data class Steps(val count: Int) : Instruction
    data class Turn(val left: Boolean) : Instruction

    enum class Direction(val value: Vector2) {
        RIGHT(UNIT_X), DOWN(UNIT_Y), LEFT(-UNIT_X), UP(-UNIT_Y);

        fun turn(left: Boolean) = when (this) {
            RIGHT -> if (left) UP else DOWN
            DOWN -> if (left) RIGHT else LEFT
            LEFT -> if (left) DOWN else UP
            UP -> if (left) LEFT else RIGHT
        }

        override fun toString() = when (this) {
            RIGHT -> ">"
            DOWN -> "v"
            LEFT -> "<"
            UP -> "^"
        }
    }

    companion object {
        fun Sequence<String>.toBoard() = Board(
            map { line ->
                Row(
                    line.indexOfAny(".#".toCharArray())..line.lastIndexOfAny(".#".toCharArray()),
                    line.mapIndexedNotNull { x, c -> x.takeIf { c == '#' } }.toSet()
                )
            }.toList()
        )

        fun String.toInstructions() = Instructions(
            StringTokenizer(this, "LR", true).asSequence()
                .map { it.toString() }
                .map {
                    when (it) {
                        "L" -> Turn(true)
                        "R" -> Turn(false)
                        else -> Steps(it.toInt())
                    }
                }
                .toList()
        )
    }
}
