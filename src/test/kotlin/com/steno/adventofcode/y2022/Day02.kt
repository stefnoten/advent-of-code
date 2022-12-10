package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec

class Day02 : AdventOfCodeSpec({ challenge ->
    challenge
        .eval(15, 13924) { lines -> lines.map { Round.parseRound(it) }.sumOf { it.score } }
        .eval(12, 13448) { lines -> lines.map { Round.parseFixedMatch(it).fix() }.sumOf { it.score } }
}) {
    enum class Choice(val score: Int) {
        ROCK(1), PAPER(2), SCISSORS(3);

        val beatedBy
            get() = values()[(ordinal + 1) % 3]
        val winsOf
            get() = values().first { it.beatedBy == this }

        infix fun against(theirs: Choice) = when {
            this == theirs -> Outcome.DRAW
            beatedBy == theirs -> Outcome.LOSE
            else -> Outcome.WIN
        }

        fun forOutcomeAgainst(outcome: Outcome, theirs: Choice) = when (outcome) {
            Outcome.WIN -> theirs.beatedBy
            Outcome.LOSE -> theirs.winsOf
            Outcome.DRAW -> theirs
        }
    }

    enum class Outcome(val score: Int) {
        DRAW(3), WIN(6), LOSE(0);
    }

    data class FixedMatch(val theirs: Choice, val outcome: Outcome) {
        fun fix() = Round(theirs, theirs.forOutcomeAgainst(outcome, theirs))
    }

    data class Round(val theirs: Choice, val yours: Choice) {
        val outcome = yours against theirs
        val score = yours.score + outcome.score

        override fun toString() = "$theirs vs $yours (${yours.score}) -> $outcome (${outcome.score}) [total: ${score}]"

        companion object {
            private val theirChoice = mapOf(
                "A" to Choice.ROCK,
                "B" to Choice.PAPER,
                "C" to Choice.SCISSORS
            )
            private val ourChoice = mapOf(
                "X" to Choice.ROCK,
                "Y" to Choice.PAPER,
                "Z" to Choice.SCISSORS,
            )
            private val outcome = mapOf(
                "X" to Outcome.LOSE,
                "Y" to Outcome.DRAW,
                "Z" to Outcome.WIN,
            )

            fun parseRound(line: String) = line.split(' ')
                .let { (a, b) -> Round(theirChoice[a]!!, ourChoice[b]!!) }

            fun parseFixedMatch(line: String) = line.split(' ')
                .let { (a, b) -> FixedMatch(theirChoice[a]!!, outcome[b]!!) }
        }
    }
}

