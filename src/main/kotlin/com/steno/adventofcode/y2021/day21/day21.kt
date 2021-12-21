package com.steno.adventofcode.y2021.day21

import com.steno.adventofcode.util.parse
import com.steno.assignment

data class Player(val id: Int, val position: Position, val score: Int = 0) {
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
        .eval { (player1, player2) -> wins(player1, player2, 21).byPlayer.maxOf { it.value } }
}

val STEPS_FREQUENCIES = (1..3).flatMap { x -> (1..3).flatMap { y -> (1..3).map { z -> x + y + z } } }
    .groupingBy { it }.eachCount()

data class Wins(val byPlayer: Map<Int, Long>) {
    constructor(player: Player, wins: Long): this(mapOf(player.id to wins))

    operator fun times(factor: Int) = Wins(byPlayer.mapValues { (_, wins) -> wins * factor })
    operator fun plus(other: Wins) = Wins(
        (byPlayer.entries.asSequence() + other.byPlayer.entries.asSequence())
            .groupingBy { it.key }
            .fold(0L) { acc, (_, wins) -> acc + wins }
    )
}

fun wins(activePlayer: Player, otherPlayer: Player, targetScore: Int): Wins = when {
    activePlayer.score >= targetScore -> Wins(activePlayer, 1)
    otherPlayer.score >= targetScore -> Wins(otherPlayer, 1)
    else -> STEPS_FREQUENCIES.entries
        .map { (steps, frequency) -> wins(otherPlayer, activePlayer.advance(steps), targetScore) * frequency }
        .reduce(Wins::plus)
}

val PATTERN = Regex("Player (\\d+) starting position: (\\d+)")
fun parse(lines: Sequence<String>) = lines.map {
    PATTERN.parse(it) { (player, position) -> Player(player.toInt(), Position(position.toInt())) }
}.toList()

infix fun Int.mod(range: ClosedRange<Int>) = (this - range.start) % (range.endInclusive - range.start + 1) + range.start
