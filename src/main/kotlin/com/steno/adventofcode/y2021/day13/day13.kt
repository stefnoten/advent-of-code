package com.steno.adventofcode.y2021.day13

import com.steno.adventofcode.util.inOrder
import com.steno.adventofcode.util.parse
import com.steno.adventofcode.util.takeWhileNot
import com.steno.assignment
import java.util.*
import kotlin.math.abs

val FOLD_PATTERN = Regex("^fold along ([xy])=(\\d+)$")

enum class Axis {
    X, Y;
}

fun Pair<Int, Int>.map(axis: Axis, fn: (Int) -> Int) = when (axis) {
    Axis.X -> copy(first = fn(first))
    Axis.Y -> copy(second = fn(second))
}

data class FoldInstruction(val axis: Axis, val value: Int)

data class Input(val paper: Paper, val instructions: Sequence<FoldInstruction>)

data class Paper(val coveredDots: Set<Pair<Int, Int>>) {
    val dotCount get() = coveredDots.size
    val maxX get() = coveredDots.maxOf { (x, _) -> x }
    val maxY get() = coveredDots.maxOf { (_, y) -> y }

    fun fold(instruction: FoldInstruction) = instruction.let { (axis, value) -> fold(axis, value) }
    fun fold(axis: Axis, value: Int) = coveredDots
        .map { point -> point.map(axis) { (value - abs(value - it)) } }
        .let { Paper(it.toSet()) }

    override fun toString() = "\n" + (0..maxY).joinToString("\n") { y ->
        (0..maxX).joinToString("") { x ->
            if (x to y in coveredDots) "#" else "."
        }
    }
}

fun main() {
    assignment("2021/day13") { parse(it) }
        .eval { (paper, instructions) ->
            instructions.take(1)
                .fold(paper, Paper::fold)
                .dotCount
        }
        .eval { (paper, instructions) ->
            instructions
                .fold(paper, Paper::fold)
        }
}

fun parse(lines: Sequence<String>) = lines.inOrder(
    { firstLines ->
        firstLines
            .takeWhileNot { it.isEmpty() }
            .map { it.split(',').let { (x, y) -> x.toInt() to y.toInt() } }
    },
    { nextLines ->
        nextLines
            .dropWhile { it.isEmpty() }
            .map {
                FOLD_PATTERN.parse(it) { (axis, value) ->
                    FoldInstruction(
                        Axis.valueOf(axis.uppercase(Locale.getDefault())),
                        value.toInt()
                    )
                }
            }
    }
).let { (dots, instructions) -> Input(Paper(dots.toSet()), instructions) }
