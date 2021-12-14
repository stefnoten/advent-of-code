package com.steno.adventofcode.y2021.day10

import com.steno.adventofcode.y2021.day10.Chunks.Corrupt
import com.steno.adventofcode.y2021.day10.Chunks.Ongoing
import com.steno.assignment
import java.util.*

val PAIRS = mapOf(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '<' to '>',
)
val CORRUPT_SCORE = mapOf(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137
)
val INCOMPLETE_SCORE = mapOf(
    ')' to 1,
    ']' to 2,
    '}' to 3,
    '>' to 4
)

private fun main() {
    assignment("2021/day10") { it.map(::parseLine) }
        .eval { lines ->
            lines
                .mapNotNull {
                    when (it) {
                        is Corrupt -> CORRUPT_SCORE[it.corruptChar]
                        else -> null
                    }
                }
                .sum()
        }
        .eval { lines ->
            val scores = lines
                .mapNotNull {
                    when (it) {
                        is Ongoing -> it.incompleteScore
                        else -> null
                    }
                }
                .sorted()
                .toList()
            scores[scores.size / 2]
        }
}

private sealed class Chunks {
    abstract fun consume(c: Char): Chunks

    data class Ongoing(val incompleteChunks: Stack<Char> = Stack()) : Chunks() {
        val incompleteScore: Long
            get() = incompleteChunks
                .map { PAIRS[it]!! }
                .foldRight(0) { c, acc -> acc * 5 + (INCOMPLETE_SCORE[c] ?: 0) }

        override fun consume(c: Char) = when {
            c.isStart -> this.also { incompleteChunks.push(c) }
            c.isEnd -> when {
                incompleteChunks.empty() -> Corrupt(c)
                PAIRS[incompleteChunks.peek()] == c -> this.also { incompleteChunks.pop() }
                else -> Corrupt(c)
            }
            else -> Corrupt(c)
        }
    }

    data class Corrupt(val corruptChar: Char) : Chunks() {
        override fun consume(c: Char) = this
    }
}

private fun parseLine(line: String) = line.fold(Ongoing(), Chunks::consume)

private val Char.isStart get() = PAIRS.containsKey(this)
private val Char.isEnd get() = PAIRS.containsValue(this)
