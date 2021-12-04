package com.steno.adventofcode.y2021

import com.steno.adventofcode.util.inOrder
import com.steno.assignment

class BoardState private constructor(private val board: Board,
                                     private val markedNumbers: List<Int>) {
    constructor(board: Board): this(board, listOf())

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

private fun main() {
    assignment("2021/day4") { parseGame(it) }
        .eval { it.winner.score }
        .eval { it.loser.score }
}

private fun parseGame(lines: Sequence<String>) = lines.inOrder(
    { it.first().let(::parseNumbers) },
    { it.filter(String::isNotEmpty).windowed(5, 5, transform = ::parseBoard).toList() },
    ::Game
)

private fun parseNumbers(line: String) = line.split(',').map { it.toInt() }

private fun parseBoard(lines: List<String>) = BoardState(Board(lines.map(::parseRow)))

private fun parseRow(line: String) = line.split(Regex("\\s+"))
    .filter { it.isNotEmpty() }
    .map { it.toInt() }
