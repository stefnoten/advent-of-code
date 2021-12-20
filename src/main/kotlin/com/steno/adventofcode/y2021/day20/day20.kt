package com.steno.adventofcode.y2021.day20

import com.steno.adventofcode.util.inOrder
import com.steno.assignment

data class Input(val lookup: List<Int>, val image: Image) {
    fun enhance() = copy(image = image.enhance { lookup[it] })
}

data class Image(val values: List<List<Int>>, val background: Int = 0) {
    val width = values.first().size
    val height = values.size
    val rangeX = 0 until width
    val rangeY = 0 until height

    val lightPixelCount get() = values.asSequence().flatten().count { it != 0 }

    fun enhance(lookUp: (Int) -> Int) = Image(
        (-2 until height + 2).map { y ->
            (-2 until width + 2).map { x ->
                lookUp(valueAt(x to y))
            }
        },
        background = lookUp((1..9).map { background }.digitsToInt())
    )

    private fun valueAt(point: Pair<Int, Int>) = point.let { (x, y) ->
        (y - 1..y + 1).flatMap { wY ->
            (x - 1..x + 1).map { wX -> this[wX to wY] }
        }.digitsToInt()
    }

    operator fun get(point: Pair<Int, Int>) = point.let { (x, y) -> if (point in this) values[y][x] else background }

    operator fun contains(point: Pair<Int, Int>) = point.let { (x, y) -> x in rangeX && y in rangeY }

    override fun toString() = "\n" + values.joinToString("\n") { row ->
        row.joinToString("") { if (it == 0) "." else "#" }
    }
}

private fun main() {
    assignment("2021/day20") { parse(it) }
        .eval { it.enhance().enhance().image.lightPixelCount }
        .eval { input -> (1..50).fold(input) { acc, _ -> acc.enhance() }.image.lightPixelCount }
}

fun parse(lines: Sequence<String>): Input = lines.inOrder {
    Input(
        next().toNumbers(),
        Image(next {
            dropWhile { it.isBlank() }
                .map { it.toNumbers() }
                .toList()
        })
    )
}

fun String.toNumbers() = map { if (it == '#') 1 else 0 }
private fun List<Int>.digitsToInt(bitsPerDigit: Int = 1) = fold(0) { acc, i -> (acc shl bitsPerDigit) + i }
