package com.steno.adventofcode.y2021.day14

import com.steno.adventofcode.util.inOrder
import com.steno.adventofcode.util.parse
import com.steno.assignment

data class Polymer(val pairs: Sequence<Pair<Char, Char>>, val rules: Map<Pair<Char, Char>, Char>) {
    val occurrences
        get() = pairs.inOrder({ it.first().let { f -> sequenceOf(f.first to f.first, f) } }, { it }).let { (a, b) -> a + b }
            .groupingBy { it.second }
            .eachCount()
    val score = occurrences.let { occ -> occ.maxOf { it.value } - occ.minOf { it.value } }

    fun steps(count: Int) = (1..count).fold(this) { acc, _ -> acc.step() }
    fun step() = pairs.flatMap { pair ->
        rules[pair]
            ?.let { sequenceOf(pair.first to it, it to pair.second) }
            ?: sequenceOf(pair)
    }.let { Polymer(it, rules) }
}

private fun main() {
    assignment("2021/day14") { parse(it) }
        .eval { polymer ->
            polymer.steps(10).score
        }
        .eval { polymer ->
            polymer.steps(40).score
        }
}

fun parse(lines: Sequence<String>) = lines.inOrder(
    { it.first().asSequence().zipWithNext() },
    { it.dropWhile(String::isEmpty).map(::parseRule) }
).let { (initial, rules) -> Polymer(initial, rules.toMap()) }

private val RULE_FORMAT = Regex("^([A-Z]{2}) -> ([A-Z])$")

fun parseRule(line: String) = RULE_FORMAT.parse(line) { (pair, inserted) -> (pair.first() to pair.last()) to inserted.first() }
