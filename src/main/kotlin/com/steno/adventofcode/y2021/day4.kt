package com.steno.adventofcode.y2021

import com.steno.adventofcode.util.inOrder
import com.steno.assignment

data class Board(private val rows: List<List<Int>>, private val columns: List<List<Int>>, private val matches: List<Int>) {
    constructor(rows: List<List<Int>>): this(rows, columns(rows), listOf())

    private val all = rows.flatten().toSet()

    val won = (rows + columns).any { line -> line.all { it in matches } }
    val score
        get() = if (won) all.filterNot { it in matches }.reduce(Int::plus) * matches.last() else 0

    fun mark(number: Int) = if (!won && number in this) copy(matches = matches.plus(number)) else this

    operator fun contains(number: Int) = all.contains(number)

    companion object {
        private fun columns(rows: List<List<Int>>) = (0 until rows[0].size)
            .map { i -> rows.map { row -> row[i] } }
    }
}

data class Game(val numbers: List<Int>, val boards: List<Board>) {
    val winner
        get() = numbers
            .runningFold(boards) { currentBoards, number -> currentBoards.map { it.mark(number) } }
            .firstNotNullOf { it.find(Board::won) }
    val loser
        get() = numbers
            .runningFold(boards) { currentBoards, number -> currentBoards.filterNot { it.won }.map { it.mark(number) } }
            .first { it.size == 1 }[0]
}

private fun main() {
    assignment("2021/day4")
        .eval { lines -> parseGame(lines).winner.score }
        .eval { lines -> parseGame(lines).loser.score }
}

private fun parseGame(lines: Sequence<String>) = lines.inOrder(
    { it.first().let(::parseNumbers) },
    { it.filter(String::isNotEmpty).windowed(5, 5, transform = ::parseBoard).toList() },
    ::Game
)

private fun parseNumbers(line: String) = line.split(',').map { it.toInt() }

private fun parseBoard(lines: List<String>) = Board(lines.map(::parseRow))

private fun parseRow(line: String) = line.split(Regex("\\s+"))
    .filter { it.isNotEmpty() }
    .map { it.toInt() }
