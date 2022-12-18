package com.steno.adventofcode.y2021

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.parse

private class Day17: AdventOfCodeSpec({ challenge ->
    challenge.map { PATTERN.parse(it.first()) { (x1, x2, y1, y2) -> Pair(x1.toInt()..x2.toInt(), y1.toInt()..y2.toInt()) } }
        .eval(112, 1566) { (xRange, yRange) ->
            val stepsWithSpeedsY = TrajectoryY.candidates(yRange)
                .flatMap { it.stepsToReachTarget.map { t -> t to it.v0 } }
                .groupBy({it.first}, {it.second})

            TrajectoryX.candidates(xRange)
                .flatMap { trajectoryX ->
                    val velocitiesInFixedSteps = trajectoryX.stepsToReachTarget.asSequence()
                        .flatMap { t -> (stepsWithSpeedsY[t] ?: emptyList()).map { vY -> trajectoryX.v0 to vY } }
                    val velocitiesRemaining = when (trajectoryX.endsInRange) {
                        false -> emptySequence()
                        true -> stepsWithSpeedsY.asSequence()
                            .filter { (t, _) -> t >= trajectoryX.stepsToFinalX }
                            .flatMap { (_, vY) -> vY.map { trajectoryX.v0 to it } }
                    }
                    velocitiesInFixedSteps + velocitiesRemaining
                }
                .distinct()
                .count()
        }
}) {
    data class TrajectoryX(val v0: Int, val targetRange: IntRange) {
        val finalX = v0 * (v0 + 1) / 2
        val stepsToFinalX = v0 + 1
        val endsInRange = finalX in targetRange
        val canReach = finalX >= targetRange.first
        val passesImmediately = v0 > targetRange.last
        val stepsToReachTarget: List<Int> by lazy {
            when (canReach && !passesImmediately) {
                true -> generateSequence(v0) { maxOf(it - 1, 0) }
                    .takeWhile { it != 0 }
                    .scan(0) { x, v -> x + v }
                    .takeWhile { it <= targetRange.last }
                    .mapIndexedNotNull { t, x -> if (x in targetRange) t else null }
                    .toList()
                false -> listOf()
            }
        }

        companion object {
            fun candidates(range: IntRange) = generateSequence(1) { it + 1 }
                .map { TrajectoryX(it, range) }
                .dropWhile { !it.canReach }
                .takeWhile { !it.passesImmediately }
                .filter { it.stepsToReachTarget.isNotEmpty() }
        }
    }

    data class TrajectoryY(val v0: Int, val targetRange: IntRange) {
        val passesImmediately = v0 < targetRange.first
        val stepsToReachTarget: List<Int> by lazy {
            when (!passesImmediately) {
                true -> generateSequence(v0) { it - 1 }
                    .scan(0) { y, v -> y + v }
                    .takeWhile { it >= targetRange.first }
                    .mapIndexedNotNull { t, y -> if (y in targetRange) t else null }
                    .toList()
                false -> listOf()
            }
        }

        companion object {
            fun candidates(range: IntRange) = generateSequence(0) { it - 1 }
                .map { TrajectoryY(it, range) }
                .takeWhile { !it.passesImmediately }
                .filter { it.stepsToReachTarget.isNotEmpty() }
                .flatMap { sequenceOf(it, it.copy(v0 = -it.v0 - 1)) }
                .distinct()
        }
    }

    companion object {
        private val PATTERN = Regex(""".*x=(-?\d+)\.\.(-?\d+), y=(-?\d+)\.\.(-?\d+)""")
    }
}
