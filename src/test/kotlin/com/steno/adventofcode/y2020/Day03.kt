package com.steno.adventofcode.y2020

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.spec.mapEach

private class Day03 : AdventOfCodeSpec({ challenge ->
    challenge.mapEach { trees(it) }
        .eval(7, 216) { treeLines -> numberOfTrees(treeLines, Step(3, 1)) }
        .map { it.toList() }
        .eval(336, 6708199680) { treeLines ->
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
}) {
    data class Step(val x: Int, val y: Int)

    companion object {
        fun numberOfTrees(treeLines: Sequence<List<Boolean>>, step: Step) =
            treeLines
                .every(step.y)
                .foldIndexed(0) { i, count, treeLine ->
                    if (treeLine[i * step.x % treeLine.size]) count + 1 else count
                }

        fun trees(line: String) = line.map { it == '#' }

        fun <T> Sequence<T>.every(n: Int) = if (n == 1) this else windowed(n, n) { it[0] }
    }
}
