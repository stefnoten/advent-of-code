package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.flatScan
import com.steno.adventofcode.util.inOrder
import com.steno.adventofcode.util.math.Vector2
import com.steno.adventofcode.util.math.Vector2.Companion.UNIT_X
import com.steno.adventofcode.util.math.Vector2.Companion.UNIT_Y
import com.steno.adventofcode.util.math.gcd
import com.steno.adventofcode.util.split
import com.steno.adventofcode.y2022.Day22.Direction.*
import org.junit.jupiter.api.Test
import java.util.StringTokenizer
import kotlin.test.assertEquals

class Day22 : AdventOfCodeSpec({ challenge ->
    challenge.map { lines ->
        lines.split { it.isEmpty() }.inOrder {
            next().toBoard() to next().first().toInstructions()
        }
    }
        .eval(6032, 191010) { (board, instructions) ->
            instructions.asSequence()
                .flatScan(Position(at = board.topLeft, direction = RIGHT)) { position, instruction ->
                    position.handle(instruction, board)
                }
                .printOn(board)
                .last().password
        }
        .map { it.copy(first = it.first.copy(wrapStrategy = WrapStrategy.ON_CUBE)) }
        .focusOn("input")
        .eval { (board, instructions) ->
            instructions.asSequence()
                .flatScan(Position(at = board.topLeft, direction = RIGHT)) { position, instruction ->
                    position.handle(instruction, board)
                }
                .printOn(board)
                .last().password
        }
}) {
    data class Row(val rangeX: IntRange, val wallsAtX: Set<Int>) {
        val first get() = rangeX.first
        val last get() = rangeX.last

        operator fun contains(x: Int) = x in rangeX

        fun isWall(x: Int) = x in wallsAtX

        fun toString(render: (x: Int, boardState: Char) -> Char) = " ".repeat(rangeX.first) + rangeX.joinToString("") { x ->
            render(x, if (isWall(x)) '#' else '.').toString()
        }
    }

    data class Board(
        val rows: List<Row>,
        val wrapStrategy: WrapStrategy = WrapStrategy.ON_MAP
    ) {
        val topLeft = Vector2(rows.first().first, 0)
        val cubeSize = gcd(rows.size, rows.maxOf { it.last + 1 })

        operator fun contains(position: Vector2) = position.y in rows.indices && position.x in rows[position.y]

        fun isWall(position: Vector2) = rows[position.y].isWall(position.x)

        fun step(position: Position): Position? {
            val dest = position.next
            val wrappedDest = if (dest.at in this) dest else wrapStrategy.wrap(dest, this)
            return wrappedDest.takeUnless { isWall(it.at) }
        }

        fun toString(render: (position: Vector2, boardState: Char) -> Char) = rows.withIndex().joinToString("\n") { (y, row) ->
            row.toString { x, state -> render(Vector2(x, y), state) }
        }
    }


    enum class WrapStrategy {
        ON_MAP {
            override fun wrap(position: Position, board: Board): Position {
                val at = position.at
                return when (position.direction) {
                    RIGHT -> position.copy(at = at.copy(x = board.rows[at.y].first))
                    LEFT -> position.copy(at = at.copy(x = board.rows[at.y].last))
                    else -> generateSequence(position) { current ->
                        current.previous.takeIf { it.at in board }
                    }.last()
                }
            }
        },
        ON_CUBE {
            override fun wrap(position: Position, board: Board): Position {
                /**      0      1     2      3
                 *
                 *           1_____2_____3
                 *           |     |     |
                 * 0         |  F  |  R  |      0
                 *           0_____|_____4
                 *           |     |
                 * 1         |  D  |            1
                 *     0_____|_____4
                 *     |     |     |
                 * 2   |  L  |  B  |            2
                 *     1_____|_____3
                 *     |     |
                 * 3   |  U  |                  3
                 *     2_____3
                 *
                 * 4                            4
                 *        0      1     2     3
                 */
                data class Case(val x: Int, val y: Int, val direction: Direction)

                val d = board.cubeSize
                val x = (position.at.x % d + d) % d
                val y = (position.at.y % d + d) % d
                val (faceX, faceY) = (position.at + Vector2(d, d)) / d - Vector2.ONE
                return when (Case(faceX, faceY, position.direction)) {
                    Case(1, -1, UP) -> Position(Vector2(0, 3) * d + Vector2(0, x), RIGHT)
                    Case(2, -1, UP) -> Position(Vector2(0, 3) * d + Vector2(x, d - 1), UP)
                    Case(3, 0, RIGHT) -> Position(Vector2(1, 2) * d + Vector2(d - 1, d - y - 1), LEFT)
                    Case(2, 1, DOWN) -> Position(Vector2(1, 1) * d + Vector2(d - 1, x), LEFT)
                    Case(2, 1, RIGHT) -> Position(Vector2(2, 0) * d + Vector2(y, d - 1), UP)
                    Case(2, 2, RIGHT) -> Position(Vector2(2, 0) * d + Vector2(d - 1, d - y - 1), LEFT)
                    Case(1, 3, DOWN) -> Position(Vector2(0, 3) * d + Vector2(d - 1, x), LEFT)
                    Case(1, 3, RIGHT) -> Position(Vector2(1, 2) * d + Vector2(y, d - 1), UP)
                    Case(0, 4, DOWN) -> Position(Vector2(2, 0) * d + Vector2(x, 0), DOWN)
                    Case(-1, 3, LEFT) -> Position(Vector2(1, 0) * d + Vector2(y, 0), DOWN)
                    Case(-1, 2, LEFT) -> Position(Vector2(1, 0) * d + Vector2(0, d - y - 1), RIGHT)
                    Case(0, 1, UP) -> Position(Vector2(1, 1) * d + Vector2(0, x), RIGHT)
                    Case(0, 1, LEFT) -> Position(Vector2(0, 2) * d + Vector2(y, 0), DOWN)
                    Case(0, 0, LEFT) -> Position(Vector2(0, 2) * d + Vector2(0, d - y - 1), RIGHT)
                    else -> throw IllegalStateException()
                }
            }
        };

        abstract fun wrap(position: Position, board: Board): Position
    }

    data class Position(val at: Vector2, val direction: Direction) {
        val password get() = 1000 * (at.y + 1) + 4 * (at.x + 1) + direction.ordinal
        val previous get() = Position(at - direction.value, direction)
        val next get() = Position(at + direction.value, direction)

        fun handle(instruction: Instruction, board: Board) = when (instruction) {
            is Turn -> turn(instruction.left)
            is Steps -> step(instruction.count, board)
        }

        fun turn(left: Boolean) = sequenceOf(copy(direction = direction.turn(left)))
        fun step(count: Int, board: Board) = generateSequence(this) {
            board.step(it)
        }.take(count + 1)
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

        fun Sequence<Position>.printOn(board: Board): Sequence<Position> =
            toList()
                .also { positions ->
                    val positionsByAt = positions.associateBy { it.at }
                    println()
                    println(board.toString { pos, c -> positionsByAt[pos]?.direction?.toString()?.first() ?: c })
                }
                .asSequence()
    }
}
