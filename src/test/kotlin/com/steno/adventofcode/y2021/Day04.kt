package com.steno.adventofcode.y2021

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.inOrder
import com.steno.adventofcode.util.split

private class Day04: AdventOfCodeSpec({ challenge ->
    challenge.map { parseGame(it) }
        .eval(4512, 5685) { it.winner.score }
        .eval(1924, 21070) { it.loser.score }
}) {
    class BoardState private constructor(
        private val board: Board,
        private val markedNumbers: List<Int>
    ) {
        constructor(board: Board) : this(board, listOf())

        private val unmarkedNumbers = board.numbers.filterNot { it in markedNumbers }

        val won = board.lines.any { line -> line.all { it in markedNumbers } }

        val score
            get() = when {
                won -> unmarkedNumbers.reduce(Int::plus) * markedNumbers.last()
                else -> 0
            }

        fun mark(number: Int) = when {
            !won && number in board -> BoardState(board, markedNumbers + number)
            else -> this
        }
    }

    class Board(private val rows: List<List<Int>>) {
        private val columns = (0 until rows[0].size)
            .map { i -> rows.map { row -> row[i] } }
        val lines = rows + columns
        val numbers = rows.flatten().toSet()

        operator fun contains(number: Int) = numbers.contains(number)
    }

    data class Game(val numbers: List<Int>, val boards: List<BoardState>) {
        val winner
            get() = numbers
                .runningFold(boards) { currentBoards, number -> currentBoards.map { it.mark(number) } }
                .firstNotNullOf { it.find(BoardState::won) }
        val loser
            get() = numbers
                .runningFold(boards) { currentBoards, number -> currentBoards.filterNot { it.won }.map { it.mark(number) } }
                .first { it.size == 1 }[0]
    }

    companion object {
        fun parseGame(lines: Sequence<String>) = lines.inOrder(
            { firstLines -> firstLines.first().let(Companion::parseNumbers) },
            { nextLines ->
                nextLines.drop(1)
                    .split { it.isEmpty() }
                    .map { parseBoard(it.toList()) }
                    .toList()
            },
            Day04::Game
        )

        fun parseNumbers(line: String) = line.split(',').map { it.toInt() }

        fun parseBoard(lines: List<String>) = BoardState(Board(lines.map(Companion::parseRow)))

        fun parseRow(line: String) = line.split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .map { it.toInt() }

    }
}

