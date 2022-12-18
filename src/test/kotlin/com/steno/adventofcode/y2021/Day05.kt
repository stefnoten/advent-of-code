package com.steno.adventofcode.y2021

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.spec.mapEach
import com.steno.adventofcode.util.parse
import kotlin.math.abs

private class Day05: AdventOfCodeSpec({ challenge ->
    challenge.mapEach { parseLine(it) }
//        .eval { lines -> lines.filterNot { it.direction.diagonal }
//            .map { "$it : ${it.start..it.end}" }
//            .drop(1)
//            .take(1)
//        }
        .eval(5, 5169) { lines ->
            pointsMarkedBy(lines.filterNot { it.direction.diagonal })
                .count { (_, count) -> count >= 2 }
        }
        .eval(12, 22083) { lines ->
            pointsMarkedBy(lines)
                .count { (_, count) -> count >= 2 }
        }
}) {
    data class Line(val start: Vector, val end: Vector) {
        val direction = end - start

        override fun toString() = "$start -> $end"
    }

    data class Vector(val x: Int, val y: Int) {
        val horizontal get() = y == 0
        val vertical get() = x == 0
        val diagonal get() = abs(x) == abs(y)

        operator fun minus(other: Vector) = Vector(other.x - x, other.y - y)

        operator fun rangeTo(other: Vector) = (other - this).let { dir ->
            when {
                dir.vertical -> (y towards other.y).map { Vector(x, it) }
                dir.horizontal -> (x towards other.x).map { Vector(it, y) }
                dir.diagonal -> (x towards other.x).zip(y towards other.y).map { (x, y) -> Vector(x, y) }
                else -> listOf()
            }
        }

        private infix fun Int.towards(other: Int) = when {
            this < other -> this..other
            else -> this  downTo other
        }

        override fun toString() = "($x,$y)"
    }

    companion object {
        fun pointsMarkedBy(lines: Sequence<Line>) = lines
            .flatMap { (start, end) -> start..end }
            .groupingBy { it }
            .eachCount()

        fun parseLine(value: String) = Regex("^(.+) -> (.+)$").parse(value) { (start, end) ->
            Line(parsePoint(start), parsePoint(end))
        }

        fun parsePoint(value: String) = Regex("^(\\d+),(\\d+)$").parse(value) { (x, y) -> Vector(x.toInt(), y.toInt()) }

    }
}
