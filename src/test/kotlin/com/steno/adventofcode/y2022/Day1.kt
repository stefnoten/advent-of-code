package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.split

class Day1 : AdventOfCodeSpec({ challenge ->
    challenge.map { Elf.parse(it) }
        .eval { elves -> elves.maxOfOrNull { it.total } }
        .eval { elves -> elves.map { it.total }.sortedDescending().take(3).sum() }
}) {
    data class Elf(val calories: List<Int>) {
        val total = calories.sum()

        companion object {
            fun parse(lines: Sequence<String>) = lines
                .split { it.isEmpty() }
                .map { elfLines -> elfLines.map { it.toInt() } }
                .map { Elf(it.toList()) }
        }
    }

}

