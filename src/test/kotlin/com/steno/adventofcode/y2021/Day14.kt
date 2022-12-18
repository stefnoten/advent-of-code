package com.steno.adventofcode.y2021

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.inOrder
import com.steno.adventofcode.util.parse

private class Day14: AdventOfCodeSpec({ challenge ->
    challenge.map { parse(it) }
        .eval(1588, 3009) { polymer ->
            polymer.steps(10).score
        }
        .eval(2188189693529, 3459822539451) { polymer ->
            polymer.steps(40).score
        }
}) {
    data class Polymer(val pairs: Map<Pair<Char, Char>, Long>, val rules: Map<Pair<Char, Char>, Char>) {
        val occurrences
            get() = pairs.asSequence().inOrder(
                { it.first().let { (pair, count) -> sequenceOf((pair.first to pair.first) to count, pair to count) } },
                { it.map { (pair, count) -> pair to count } }
            )
                .let { (a, b) -> a + b }
                .groupingBy { (pair, _) -> pair.second }
                .fold(0L) { acc, (_, count) -> acc + count }

        val score = occurrences.let { occ -> occ.maxOf { it.value } - occ.minOf { it.value } }

        fun steps(count: Int) = (1..count).fold(this) { acc, _ -> acc.step() }
        fun step() = pairs.asSequence()
            .flatMap { (pair, count) ->
                when (val inserted = rules[pair]) {
                    null -> sequenceOf(pair to count)
                    else -> sequenceOf((pair.first to inserted) to count, (inserted to pair.second) to count)
                }
            }
            .groupingBy { (pair, _) -> pair }
            .fold(0L) { accumulator, (_, count) -> accumulator + count }
            .let { Polymer(it, rules) }
    }

    companion object {
        fun parse(lines: Sequence<String>) = lines.inOrder(
            {
                it.first().asSequence()
                    .zipWithNext()
                    .groupingBy { pair -> pair }
                    .eachCount()
                    .mapValues { (_, v) -> v.toLong() }
            },
            { it.dropWhile(String::isEmpty).map(Companion::parseRule) }
        ).let { (initial, rules) -> Polymer(initial, rules.toMap()) }

        val RULE_FORMAT = Regex("^([A-Z]{2}) -> ([A-Z])$")

        fun parseRule(line: String) = RULE_FORMAT.parse(line) { (pair, inserted) -> (pair.first() to pair.last()) to inserted.first() }

    }
}

