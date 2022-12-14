package com.steno.adventofcode.y2021

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.spec.mapEach
import com.steno.adventofcode.util.untilStable

private class Day08: AdventOfCodeSpec({ challenge ->
    challenge.mapEach { parseInput(it) }
        .eval(26, 0, 397) { examples ->
            UNIQUE_DIGITS.keys.map { it.segmentCount }.let { uniqueCounts ->
                examples
                    .flatMap { it.requestedDigits }
                    .count { it.segmentCount in uniqueCounts }
            }
        }
        .eval(61229, 5353, 1027422) { examples ->
            examples
                .map { example ->
                    segmentMappings(example).let { mapping ->
                        example.requestedDigits.map { it.map(mapping) }
                    }
                }
                .map { digits -> digits.fold(0) { acc, digit -> acc * 10 + DIGITS[digit]!! } }
                .sum()
        }
}) {
    data class Digit(val segments: Set<Char>) {
        val segmentCount = segments.size

        fun map(mapping: Map<Char, Char>) = Digit(segments.map { mapping[it]!! }.toSet())

        operator fun contains(segment: Char) = segment in segments

        override fun toString() = segments.joinToString("")
    }

    data class Input(val allDigits: List<Digit>, val requestedDigits: List<Digit>)

    data class Mapping(val segments: Set<Char>, val candidates: Set<Set<Char>>) {
        val resolution = candidates.singleOrNull()
        val unambiguous = resolution != null

        fun simplifyAll(mappings: Collection<Mapping>) = mappings.fold(this) { acc, next -> acc.simplify(next) }
        fun simplify(mapping: Mapping) = when {
            mapping.segments == segments -> Mapping(segments, mapping.candidates intersect candidates)
            mapping.segments in segments && mapping.unambiguous -> Mapping(
                segments - mapping.segments,
                candidates
                    .filter { mapping.resolution!! in it }
                    .map { it - mapping.resolution!! }
                    .toSet()
            )
            else -> this
        }

        override fun toString() = "${valueOf(segments)} -> ${candidates.map { valueOf(it) }}"
        fun valueOf(chars: Set<Char>) = chars.sorted().joinToString("")
    }

    companion object {
        val DIGITS = mapOf(
            Digit("abcefg".toSet()) to 0,
            Digit("cf".toSet()) to 1,
            Digit("acdeg".toSet()) to 2,
            Digit("acdfg".toSet()) to 3,
            Digit("bcdf".toSet()) to 4,
            Digit("abdfg".toSet()) to 5,
            Digit("abdefg".toSet()) to 6,
            Digit("acf".toSet()) to 7,
            Digit("abcdefg".toSet()) to 8,
            Digit("abcdfg".toSet()) to 9,
        )

        val UNIQUE_DIGITS = DIGITS.filterKeys { digit -> DIGITS.keys.count { it.segmentCount == digit.segmentCount } == 1 }


        fun segmentMappings(example: Input) = generateSequence(example.allDigits.map { Mapping(it.segments, segmentsWithCount(it)) }) { mappings ->
            mappings.map { it.simplifyAll(mappings) }
        }
            .untilStable()
            .last()
            .toSet()
            .associate { it.segments.single() to it.resolution!!.single() }

        operator fun <T> Set<T>.contains(other: Set<T>) = other.all { it in this }

        fun segmentsWithCount(it: Digit) = digitsWithSegmentCount(it).map(Digit::segments).toSet()
        fun digitsWithSegmentCount(digit: Digit) = DIGITS.filterKeys { digit.segmentCount == it.segmentCount }.keys.toSet()

        fun parseInput(line: String) = line.split(" | ").map(Companion::parseDigits).let { (all, requested) -> Input(all, requested) }

        fun parseDigits(text: String) = text.split(' ').map { Digit(it.toSet()) }

    }
}
