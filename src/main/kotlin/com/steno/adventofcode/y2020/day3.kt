package com.steno.adventofcode.y2020

import com.steno.assignment
import com.steno.evalList

data class Step(val x: Int, val y: Int)

private fun main() {
    assignment("2020/day3") { lines -> lines.map(::trees) }
        .eval { treeLines -> numberOfTrees(treeLines, Step(3, 1)) }
        .evalList { treeLines ->
            sequenceOf(
                Step(1, 1),
                Step(3, 1),
                Step(5, 1),
                Step(7, 1),
                Step(1, 2),
            )
                .map { numberOfTrees(treeLines.asSequence(), it) }
                .map { it.toLong() }.reduce(Long::times)
        }
}

private fun numberOfTrees(treeLines: Sequence<List<Boolean>>, step: Step) =
    treeLines
        .every(step.y)
        .foldIndexed(0) { i, count, treeLine ->
            if (treeLine[i * step.x % treeLine.size]) count + 1 else count
        }

fun trees(line: String) = line.map { it == '#' }

fun <T> Sequence<T>.every(n: Int) = if (n == 1) this else windowed(n, n) { it[0] }
