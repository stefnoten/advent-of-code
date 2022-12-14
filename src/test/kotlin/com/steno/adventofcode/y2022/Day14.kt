package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.math.Vector2
import com.steno.adventofcode.y2022.Day14.Cave.Companion.toCave

class Day14 : AdventOfCodeSpec({ challenge ->
    val source = Vector2(500, 0)
    challenge.map { it.toCave() }
        .eval(24, 1003) { initial ->
            generateSequence(initial) { it.withDroppedSand(source) }
                .onEachIndexed { i, cave -> println("$i\n$cave") }
                .count() - 1
        }
        .map { it.copy(floor = it.rangeY.last + 2) }
        .eval(93, 25771) { initial ->
            generateSequence(initial) { it.withDroppedSand(source) }
                .count() - 1
        }
}) {
    data class Cave(
        val rocks: Map<Int, List<IntRange>>,
        val sand: Map<Int, List<IntRange>> = mapOf(),
        val floor: Int? = null
    ) {
        private fun occupiedAtX(x: Int) = sequenceOf(rocks[x], sand[x], floor?.let { listOf(it..it) })
            .filterNotNull()
            .flatten()

        fun withDroppedSand(from: Vector2): Cave? = dropSand(from)
            ?.takeIf { !it.isAbyss }
            ?.let { (x, y) -> copy(sand = sand + (x to sand[x].addAdjoining(y))) }

        private fun List<IntRange>?.addAdjoining(y: Int): List<IntRange> {
            var found = false
            return (this ?: emptyList())
                .map {
                    if (y == it.first - 1) {
                        found = true
                        (it.first - 1)..it.last
                    } else
                        it
                }
                .let { if (found) it else (it + listOf(y..y)) }
        }

        fun dropSand(from: Vector2): Vector2? = dropSandAtX(from)
            ?.let { dest ->
                if (dest.isAbyss)
                    dest
                else
                    dropSand(dest + DOWN_LEFT) ?: dropSand(dest + DOWN_RIGHT) ?: dest
            }

        fun dropSandAtX(from: Vector2): Vector2? = occupiedAtX(from.x)
            .filter { it.first > from.y || from.y in it }
            .minOfOrNull { it.first }
            .let {
                when {
                    it == null -> ABYSS
                    it > from.y -> it - 1
                    else -> null
                }
            }
            ?.let { Vector2(from.x, it) }

        private val Vector2.isAbyss get() = y == ABYSS

        val rangeX: IntRange
            get() = (rocks.keys + sand.keys).let { it.min()..it.max() }

        val rangeY: IntRange
            get() = (rocks.values + sand.values)
                .flatten()
                .let { range -> 0..range.maxOf { it.last } }

        override fun toString(): String = rangeY.joinToString("\n") { y ->
            rangeX.joinToString("") { x ->
                when {
                    rocks[x]?.any { y in it } ?: false -> "#"
                    sand[x]?.any { y in it } ?: false -> "o"
                    else -> "."
                }
            }
        } + "\n"

        companion object {
            val DOWN_LEFT = Vector2(-1, 1)
            val DOWN_RIGHT = Vector2(1, 1)
            const val ABYSS = Int.MAX_VALUE

            fun Sequence<String>.toCave() = flatMap { it.toLines() }
                .flatMap { (a, b) ->
                    range(a.x, b.x).asSequence().map { x -> x to range(a.y, b.y) }
                }
                .groupBy({ it.first }, { it.second })
                .let { Cave(it) }

            private fun String.toLines() = splitToSequence(" -> ")
                .map { it.split(",") }
                .map { (x, y) -> Vector2(x.toInt(), y.toInt()) }
                .zipWithNext()

            private fun range(a: Int, b: Int) = if (a < b) a..b else b..a
        }
    }
}
