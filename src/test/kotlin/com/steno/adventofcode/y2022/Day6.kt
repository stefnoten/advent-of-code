package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec

class Day6 : AdventOfCodeSpec({ challenge ->
    challenge.map { it.first() }
        .eval { line -> line.indexOfMarker(4) + 4 }
        .eval { line -> line.indexOfMarker(14) + 14 }
})

private fun String.indexOfMarker(size: Int) = asSequence()
    .windowed(size)
    .indexOfFirst { it.toSet().size == size }
