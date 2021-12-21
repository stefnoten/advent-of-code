package com.steno.adventofcode.y2021.day21

import com.steno.adventofcode.util.parse
import com.steno.assignment

data class Player(val number: Int, val position: Position, val score: Int = 0) {
    fun advance(steps: Int) = (position + steps).let {
        copy(position = it, score = score + it.value)
    }
}

data class Position(val value: Int) {
    operator fun plus(other: Int) = copy(value = value + other mod 1..10)
}

private fun dice() = sequence {
    while (true) {
        yieldAll(1..100)
    }
}

private fun main() {
    assignment("2021/day21") { parse(it) }
        .eval { initial ->
            dice().chunked(3).map { it.sum() }
                .runningFoldIndexed(initial) { i, acc, diceValue ->
                    acc.mapIndexed { j, player ->
                        when (i % initial.size) {
                            j -> player.advance(diceValue)
                            else -> player
                        }
                    }
                }
                .withIndex()
                .first { (_, players) -> players.any { it.score >= 1000 } }
                .let { (i, players) ->
                    players.find { it.score < 1000 }!!.let { loser ->
                        loser.score * i * 3
                    }
                }
        }
}

val PATTERN = Regex("Player (\\d+) starting position: (\\d+)")
fun parse(lines: Sequence<String>) = lines.map {
    PATTERN.parse(it) { (player, position) -> Player(player.toInt(), Position(position.toInt())) }
}.toList()

infix fun Int.mod(range: ClosedRange<Int>) = (this - range.start) % (range.endInclusive - range.start + 1) + range.start
