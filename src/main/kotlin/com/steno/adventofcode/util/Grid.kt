package com.steno.adventofcode.util

import com.steno.adventofcode.util.math.Vector2
import com.steno.adventofcode.util.math.Vector2Range
import com.steno.adventofcode.util.math.VectorRange

data class Grid<T>(private val values: List<List<T>>) {
    val width = values.first().size
    val height = values.size
    val rangeX = 0 until width
    val rangeY = 0 until height
    val rows: Lines<T> = Rows()
    val columns: Lines<T> = Columns()

    val indices: Sequence<Vector2>
        get() = rangeY.asSequence().flatMap { y -> rangeX.asSequence().map { x -> Vector2(x, y) } }

    fun indicesFrom(first: Vector2, step: Vector2) = generateSequence(first) { it + step }.takeWhile { it in this }

    operator fun get(point: Vector2) = point.let { (x, y) -> get(x, y) }
    operator fun get(x: Int, y: Int) = values[y][x]
    operator fun contains(point: Vector2) = point.let { (x, y) -> x in rangeX && y in rangeY }

    interface Lines<T> {
        val size: Int
        val first get() = get(0)
        val last get() = get(size - 1)
        operator fun get(i: Int): Line<T>
    }

    private inner class Rows : Lines<T> {
        override val size get() = height
        override fun get(i: Int) = Row(i)
    }

    private inner class Columns : Lines<T> {
        override val size get() = width
        override fun get(i: Int) = Column(i)
    }

    interface Line<T> : Iterable<T> {
        val indices: VectorRange<Vector2>
        operator fun get(i: Int): T
    }

    private inner class Row(val y: Int) : Line<T> {
        override val indices get() = Vector2Range(rangeX, y)
        override fun get(i: Int) = this@Grid[i, y]
        override fun iterator() = values[y].iterator()
    }

    private inner class Column(val x: Int) : Line<T> {
        override val indices get() = Vector2Range(x, rangeY)
        override fun get(i: Int) = this@Grid[x, i]
        override fun iterator() = rangeY.asSequence().map { y -> this@Grid[x, y] }.iterator()
    }
}
