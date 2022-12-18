package com.steno.adventofcode.y2021

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.findKey
import com.steno.adventofcode.util.inOrder
import com.steno.adventofcode.util.math.Matrix3
import com.steno.adventofcode.util.math.Vector3
import com.steno.adventofcode.util.math.Vector3.Companion.ZERO
import com.steno.adventofcode.util.parse
import com.steno.adventofcode.util.split
import kotlin.math.abs

private class Day19: AdventOfCodeSpec({ challenge ->
    challenge.map { parse(it) }
        .eval(79, 454) { allReports ->
            reconciliateAll(allReports.toList())
                .map { it.value.beacons }
                .reduce { a, b -> a + b }
                .count()
        }
        .eval(3621, 10813) { allReports ->
            reconciliateAll(allReports.toList())
                .map { it.value.scannerAt }
                .let { scannerPositions ->
                    scannerPositions.asSequence()
                        .flatMap { first -> scannerPositions.map { second -> first to second } }
                        .maxOf { (a, b) -> (b - a).let { (dx, dy, dz) -> abs(dx) + abs(dy) + abs(dz) } }
                }
        }
}) {
    data class ScannerReport(val scannerId: Int, val scannerAt: Vector3 = ZERO, val beacons: Set<Vector3>) {
        fun inBasis(basis: Matrix3) = copy(
            scannerAt = basis * scannerAt,
            beacons = beacons.map { basis * it }.toSet()
        )

        fun reconciliateInAnyBase(other: ScannerReport) = BASES.asSequence()
            .map { inBasis(it) }
            .firstNotNullOfOrNull { it.reconciliate(other) }

        private fun reconciliate(other: ScannerReport) = beacons
            .flatMap { myBeacon -> other.beacons.map { it - myBeacon } }
            .groupingBy { it }.eachCount()
            .findKey { _, matchCount -> matchCount >= 12 }
            ?.let { offset ->
                copy(
                    scannerAt = offset,
                    beacons = beacons.map { it + offset }.toSet()
                )
            }

        override fun toString() = "--- scanner $scannerId ---\n" +
                beacons.joinToString("\n") { (x, y, z) -> "$x,$y,$z" }
    }

    companion object {
        val ALL_UNITS = Matrix3.IDENTITY.basis.toList().flatMap { sequenceOf(it, -it) }
        val BASES = ALL_UNITS.flatMap { unit1 ->
            ALL_UNITS
                .filter { unit2 -> unit1 dot unit2 == 0 }
                .map { unit2 -> Matrix3.basis(unit1, unit2, unit1 cross unit2) }
        }


        fun reconciliateAll(allReports: List<ScannerReport>) = generateSequence(allReports.first().let { mapOf(it.scannerId to it) }) { reconciliatedReports ->
            allReports
                .filter { it.scannerId !in reconciliatedReports }
                .firstNotNullOfOrNull { candidateReport ->
                    reconciliatedReports.values.firstNotNullOfOrNull { reconciliatedReport ->
                        candidateReport.reconciliateInAnyBase(reconciliatedReport)
                    }
                }
                ?.let { reconciliatedReports + (it.scannerId to it) }
        }.last()

        val SCANNER_PATTERN = Regex("--- scanner (\\d+) ---")
        fun parse(lines: Sequence<String>) = lines.split { it.isEmpty() }
            .map { scannerLines ->
                scannerLines.inOrder {
                    ScannerReport(
                        scannerId = SCANNER_PATTERN.parse(next { first() }) { (id) -> id.toInt() },
                        beacons = next { map { parseVector3(it) }.toSet() }
                    )
                }
            }

        fun parseVector3(value: String) = value
            .split(',')
            .let { (x, y, z) -> Vector3(x.toInt(), y.toInt(), z.toInt()) }

    }
}
