package com.steno.adventofcode.y2021

import com.steno.adventofcode.util.toDigits
import com.steno.assignment

data class Octopus(val level: Int, val justStartedFlashing: Boolean = false) {
    val flashing = level > 9

    fun step(steps: Int = 1) = when (steps) {
        0 -> this
        else -> Octopus(level + steps, !flashing && level + steps > 9)
    }

    fun reset() = when (flashing) {
        true -> Octopus(0)
        else -> this
    }

    override fun toString() = when {
        justStartedFlashing -> "\u001B[31m0\u001B[0m"
        flashing -> "\u001B[32m0\u001B[0m"
        else -> "$level"
    }
}

data class Grid(val lines: List<List<Octopus>>) {
    val width = lines.first().size
    val height = lines.size
    val rangeX = 0 until width
    val rangeY = 0 until height

    val pointsJustStartedFlashing
        get() = lines.flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, octopus ->
                (x to y).takeIf { octopus.justStartedFlashing }
            }
        }.toSet()
    val flashingCount
        get() = lines.sumOf { line -> line.count { it.flashing } }
    val allFlashing
        get() = lines.all { line -> line.all { it.flashing } }

    fun fullStep() = reset().step().propagateFlashes()
    fun step() = map { _, octopus -> octopus.step() }
    fun propagateFlashes() = generateSequence(this) {
        it.propagateFlashesOnce()
    }
        .dropWhile { it.pointsJustStartedFlashing.isNotEmpty() }
        .firstOrNull() ?: this

    fun propagateFlashesOnce() = pointsJustStartedFlashing.let { causes ->
        val propagatedFlashes = causes
            .flatMap { neighbours(it) }
            .groupingBy { it }.eachCount()
        map { point, octopus ->
            when (point) {
                in causes -> octopus.copy(level = octopus.level + (propagatedFlashes[point] ?: 0), justStartedFlashing = false)
                else -> octopus.step(propagatedFlashes[point] ?: 0)
            }
        }
    }

    fun reset() = map { _, octopus -> octopus.reset() }

    fun map(fn: (Pair<Int, Int>, Octopus) -> Octopus) = lines.mapIndexed { y, line ->
        line.mapIndexed { x, octopus ->
            fn(x to y, octopus)
        }
    }.let { Grid(it) }

    private fun neighbours(point: Pair<Int, Int>) = point.let { (x, y) ->
        sequenceOf(
            -1 to -1, 0 to -1, 1 to -1,
            -1 to 0, 1 to 0,
            -1 to 1, 0 to 1, 1 to 1,
        )
            .map { (dx, dy) -> x + dx to y + dy }
            .filter { it in this }
    }

    operator fun get(point: Pair<Int, Int>) = point.let { (x, y) -> lines[y][x] }
    operator fun contains(point: Pair<Int, Int>) = point.let { (x, y) -> x in rangeX && y in rangeY }

    override fun toString() = "\n" + lines.joinToString("\n") {
        it.joinToString("")
    }
}

private fun main() {
    assignment("2021/day11") { Grid(it.toDigits(::Octopus).toList()) }
        .eval { initial ->
            generateSequence(initial) { it.fullStep() }
                .take(101)
                .sumOf { it.flashingCount }
        }
        .eval { initial ->
            generateSequence(initial) { it.fullStep() }
                .indexOfFirst { it.allFlashing }
        }
}
