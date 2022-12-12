package com.steno.adventofcode.util

import com.steno.adventofcode.util.math.Vector2
import com.steno.adventofcode.util.math.Vector2.Companion.UNIT_X
import com.steno.adventofcode.util.math.Vector2.Companion.UNIT_Y
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

    fun neighboursOf(point: Vector2) = Neighbours(point)
    fun navigate(start: Vector2) = Navigator(start)

    inner class Neighbours(val point: Vector2): Iterable<Vector2> {
        val left: Vector2? get() = neighbour(- UNIT_X)
        val right: Vector2? get() = neighbour(UNIT_X)
        val up: Vector2? get() = neighbour(UNIT_Y)
        val down: Vector2? get() = neighbour(- UNIT_Y)
        override fun iterator(): Iterator<Vector2> = sequenceOf(left, right, up, down).filterNotNull().iterator()
        private fun neighbour(direction: Vector2) = (point + direction).takeIf { it in this@Grid }
    }

    inner class Navigator(val point: Vector2) {
        val left: Navigator? get() = Navigator(point - UNIT_X).takeIf { it.point in this@Grid }
        val right: Navigator? get() = Navigator(point + UNIT_X).takeIf { it.point in this@Grid }
        val up: Navigator? get() = Navigator(point + UNIT_Y).takeIf { it.point in this@Grid }
        val down: Navigator? get() = Navigator(point - UNIT_Y).takeIf { it.point in this@Grid }
        val neighbours: Sequence<Navigator> get() = sequenceOf(left, right, up, down).filterNotNull()

        val value: T get() = this@Grid[point]
    }

    interface Lines<T>: Iterable<Line<T>> {
        val size: Int
        val first get() = get(0)
        val last get() = get(size - 1)
        operator fun get(i: Int): Line<T>
        override fun iterator(): Iterator<Line<T>> = (0 until size).asSequence().map { get(it) }.iterator()
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
