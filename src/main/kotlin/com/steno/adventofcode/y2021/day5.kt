package com.steno.adventofcode.y2021

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
            dir.vertical -> range(y, other.y).map { Vector(x, it) }
            dir.horizontal -> range(x, other.x).map { Vector(it, y) }
            dir.diagonal -> range(x, other.x).zip(range(y, other.y)).map { (x, y) -> Vector(x, y) }
            else -> listOf()
        }
    }

    private fun range(start: Int, end: Int) = when {
        start < end -> start..end
        else -> start downTo end
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
