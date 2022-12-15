package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.spec.mapEach
import com.steno.adventofcode.util.math.Vector2
import com.steno.adventofcode.util.parse
import com.steno.adventofcode.y2022.Day15.Sensor.Companion.toSensor
import kotlin.math.abs

private val Vector2.tuningFrequency: Long get() = x * 4000000L + y

class Day15 : AdventOfCodeSpec({ challenge ->
    val ys = listOf(10, 2000000).iterator()
    val maxs = listOf(20, 4000000).iterator()

    challenge.mapEach { it.toSensor() }.map { Caves(it.toList()) }
        .eval(26, 5335787) { it.xsWithoutBeacon(ys.next()).count() }
        .eval(56000011L, 13673971349056L) {
            val range = 0..maxs.next()
            it.findBeaconIn(range, range).first().tuningFrequency
        }
}) {
    data class Sensor(val at: Vector2, val beacon: Vector2) {
        val distance = (beacon - at).norm1

        fun rangeAtY(y: Int) = (distance - abs(y - at.y))
            .takeIf { d -> d > 0 }
            ?.let { d -> (at.x - d)..(at.x + d) }

        companion object {
            private val FORMAT = Regex("""Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""")

            fun String.toSensor() = FORMAT.parse(this) { (sx, sy, bx, by) ->
                Sensor(Vector2(sx.toInt(), sy.toInt()), Vector2(bx.toInt(), by.toInt()))
            }
        }
    }

    data class Caves(val sensors: List<Sensor>) {
        fun xsWithoutBeacon(y: Int) = (coveredXsAtY(y) - beaconXsAtY(y).toSet())

        fun findBeaconIn(searchRangeX: IntRange, searchRangeY: IntRange): Sequence<Vector2> = searchRangeY.asSequence()
            .mapNotNull { y ->
                var x = searchRangeX.first
                for (range in coveredXRangesAtY(y).sortedBy { it.first }) {
                    when {
                        range.last <= x -> continue
                        x in range -> x = range.last + 1
                        else -> return@mapNotNull Vector2(x, y)
                    }
                }
                null
            }

        private fun beaconXsAtY(y: Int) = sensors.map { it.beacon }
            .filter { it.y == y }
            .map { it.x }

        private fun coveredXsAtY(y: Int) = coveredXRangesAtY(y).flatten().distinct()

        private fun coveredXRangesAtY(y: Int): Sequence<IntRange> = sensors.asSequence()
            .mapNotNull { it.rangeAtY(y) }
    }
}
