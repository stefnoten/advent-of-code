package com.steno.adventofcode.y2021.day5

import com.steno.adventofcode.util.parse
import com.steno.assignment
import kotlin.math.abs

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
        else -> other downTo this
    }

    override fun toString() = "($x,$y)"
}

private fun main() {
    assignment("2021/day5") { it.map(::parseLine) }
        .eval { lines ->
            pointsMarkedBy(lines.filterNot { it.direction.diagonal })
                .count { (_, count) -> count >= 2 }
        }
        .eval { lines ->
            pointsMarkedBy(lines)
                .count { (_, count) -> count >= 2 }
        }
}

private fun pointsMarkedBy(lines: Sequence<Line>) = lines
    .flatMap { (start, end) -> start..end }
    .groupingBy { it }
    .eachCount()

private fun parseLine(value: String) = Regex("^(.+) -> (.+)$").parse(value) { (start, end) ->
    Line(parsePoint(start), parsePoint(end))
}

private fun parsePoint(value: String) = Regex("^(\\d+),(\\d+)$").parse(value) { (x, y) -> Vector(x.toInt(), y.toInt()) }
