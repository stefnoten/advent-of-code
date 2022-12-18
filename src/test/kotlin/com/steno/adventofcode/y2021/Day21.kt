package com.steno.adventofcode.y2021

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.asSupplier
import com.steno.adventofcode.util.cycle
import com.steno.adventofcode.util.memoize
import com.steno.adventofcode.util.parse


private class Day21 : AdventOfCodeSpec({ challenge ->
    challenge.map { parse(it) }
        .eval(739785, 679329) { (player1, player2) -> losingScore(player1, player2, 1000, cycle(1..100).asSupplier()) }
        .eval(444356092776315, 433315766324816) { (player1, player2) -> wins(player1, player2, 21).byPlayer.maxOf { it.value } }
}) {
    data class Player(val id: Int, val position: Position, val score: Int = 0) {
        fun advance(steps: Int) = (position + steps).let {
            copy(position = it, score = score + it.value)
        }
    }

    data class Position(val value: Int) {
        operator fun plus(other: Int) = copy(value = value + other mod 1..10)
    }

    data class Wins(val byPlayer: Map<Int, Long>) {
        constructor(player: Player, wins: Long) : this(mapOf(player.id to wins))

        operator fun times(factor: Int) = Wins(byPlayer.mapValues { (_, wins) -> wins * factor })
        operator fun plus(other: Wins) = Wins(
            (byPlayer.entries.asSequence() + other.byPlayer.entries.asSequence())
                .groupingBy { it.key }
                .fold(0L) { acc, (_, wins) -> acc + wins }
        )
    }

    companion object {
        val PATTERN = Regex("Player (\\d+) starting position: (\\d+)")
        fun parse(lines: Sequence<String>) = lines.map {
            PATTERN.parse(it) { (player, position) -> Player(player.toInt(), Position(position.toInt())) }
        }.toList()

        infix fun Int.mod(range: ClosedRange<Int>) = (this - range.start) % (range.endInclusive - range.start + 1) + range.start

        val STEPS_FREQUENCIES = (1..3).flatMap { x -> (1..3).flatMap { y -> (1..3).map { z -> x + y + z } } }
            .groupingBy { it }.eachCount()

        fun losingScore(player1: Player, player2: Player, targetScore: Int, throwDie: () -> Int): Int {
            fun losingScore(activePlayer: Player, otherPlayer: Player, throws: Int): Int = when {
                activePlayer.score >= targetScore -> otherPlayer.score * throws
                otherPlayer.score >= targetScore -> activePlayer.score * throws
                else -> losingScore(
                    otherPlayer,
                    activePlayer.advance(throwDie() + throwDie() + throwDie()),
                    throws + 3
                )
            }
            return losingScore(player1, player2, 0)
        }

        val wins: (activePlayer: Player, otherPlayer: Player, targetScore: Int) -> Wins by memoize { activePlayer, otherPlayer, targetScore ->
            when {
                activePlayer.score >= targetScore -> Wins(activePlayer, 1)
                otherPlayer.score >= targetScore -> Wins(otherPlayer, 1)
                else -> STEPS_FREQUENCIES.entries
                    .map { (steps, frequency) -> wins(otherPlayer, activePlayer.advance(steps), targetScore) * frequency }
                    .reduce(Wins::plus)
            }
        }

    }
}
