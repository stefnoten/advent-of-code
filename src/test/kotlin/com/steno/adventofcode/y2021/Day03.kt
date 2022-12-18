package com.steno.adventofcode.y2021

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.toDigits

private class Day03: AdventOfCodeSpec({ challenge ->
    challenge.map { lines -> lines.toDigits() }
        .eval(198, 3429254) { it.map(Day03::Stats).reduce(Stats::plus).powerConsumption }
        .map { it.toList() }
        .eval(230, 5410338) { allDigits ->
            val oxygenGeneratorRating = (0 until allDigits[0].count())
                .fold(allDigits) { candidates, index -> retainWithSameDigitAt(candidates, index, stats(candidates).mostCommonDigits) }[0]
                .let { digitsToInt(it) }
            val co2ScrubberRating = (0 until allDigits[0].count())
                .fold(allDigits) { candidates, index -> retainWithSameDigitAt(candidates, index, stats(candidates).leastCommonDigits) }[0]
                .let { digitsToInt(it) }
            oxygenGeneratorRating * co2ScrubberRating
        }
}) {
    data class Stats(val count: Int, val digitCounts: List<Int>) {
        constructor(values: List<Int>) : this(1, values)

        val mostCommonDigits
            get() = digitCounts.map { if (it * 2 < count) 0 else 1 }
        val leastCommonDigits
            get() = mostCommonDigits.map { 1 - it }
        private val gammaRate
            get() = digitsToInt(mostCommonDigits)
        private val epsilonRate
            get() = digitsToInt(leastCommonDigits)

        val powerConsumption
            get() = gammaRate * epsilonRate

        operator fun plus(other: Stats) = Stats(count + other.count, digitCounts.zip(other.digitCounts, Int::plus))
    }

    companion object {
        fun stats(data: List<List<Int>>) = data.map(Day03::Stats).reduce(Stats::plus)

        fun retainWithSameDigitAt(candidates: List<List<Int>>, index: Int, targetDigits: List<Int>): List<List<Int>> {
            return if (candidates.size == 1)
                candidates
            else
                candidates.filter { it[index] == targetDigits[index] }
        }

        fun digitsToInt(digits: List<Int>) = digits.joinToString("").toInt(2)
    }
}
