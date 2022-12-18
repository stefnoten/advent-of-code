package com.steno.adventofcode.y2020

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.spec.mapEach

private class Day01 : AdventOfCodeSpec({ challenge ->
    challenge.mapEach { it.toInt() }
        .map { it.toSet() }
        .eval(514579, 440979) { input ->
            input
                .let { it.find { i -> 2020 - i in it } }!!
                .let { it * (2020 - it) }
        }
        .eval(241861950, 82498112) { input ->
            input.firstNotNullOf { i ->
                input.find { j -> 2020 - i - j in input }
                    ?.let { j -> i * j * (2020 - i - j) }
            }
        }
})
