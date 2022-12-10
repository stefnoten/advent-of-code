package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.spec.mapEach
import com.steno.adventofcode.util.contains
import com.steno.adventofcode.util.intersect
import com.steno.adventofcode.util.isNotEmpty

class Day04 : AdventOfCodeSpec({ challenge ->
    challenge.mapEach { ElfAssignments.parse(it) }
        .eval(2, 530) { assignment -> assignment.count { it.fullyOverlapping } }
        .eval(4, 903) { assignment -> assignment.count { it.overlaps } }
}) {
    data class ElfAssignments(val elf1: IntRange, val elf2: IntRange) {
        val fullyOverlapping = elf2 in elf1 || elf1 in elf2
        val overlaps = (elf1 intersect elf2).isNotEmpty()

        companion object {
            fun parse(line: String) = line.split(',')
                .map { parseRange(it) }
                .let { (a, b) -> ElfAssignments(a, b) }

            private fun parseRange(range: String) = range
                .split('-')
                .map { it.toInt() }
                .let { (a, b) -> a..b }
        }
    }
}
