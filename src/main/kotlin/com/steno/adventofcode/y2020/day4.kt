package com.steno.adventofcode.y2020

import com.steno.adventofcode.util.split
import com.steno.assignment

data class Passport(val fields: Map<String, String>) {
    val hasRequiredFields
        get() = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid").all { it in fields }
    val hasValidFields
        get() = sequenceOf(
            validBirthYear,
            validIssueYear,
            validExpirationYear,
            validHeight,
            validHairColor,
            validEyeColor,
            validPasswordId
        ).none { it == null }

    val validBirthYear = fields["byr"]?.toIntOrNull()?.takeIf { it in 1920..2002 }
    val validIssueYear = fields["iyr"]?.toIntOrNull()?.takeIf { it in 2010..2020 }
    val validExpirationYear = fields["eyr"]?.toIntOrNull()?.takeIf { it in 2020..2030 }
    val validHeight = fields["hgt"]
        ?.let { Regex("^(\\d+)([^\\d]+)$").matchEntire(it)?.destructured }
        ?.takeIf { (size, unit) ->
            when (unit) {
                "cm" -> size.toInt() in 150..193
                else -> size.toInt() in 59..76
            }
        }
    val validHairColor = fields["hcl"]?.takeIf { it.matches(Regex("^#[0-9a-f]{6}$")) }
    val validEyeColor = fields["ecl"]?.takeIf { it in setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth") }
    val validPasswordId = fields["pid"]?.takeIf { it.matches(Regex("^\\d{9}$")) }
}

private fun main() {
    assignment("2020/day4") { parsePassports(it) }
        .eval { it.filter(Passport::hasRequiredFields).count() }
        .eval { it.filter(Passport::hasValidFields).count() }
}

private fun parsePassports(lines: Sequence<String>) = lines.split { it.isEmpty() }.map(::parsePassport)

private fun parsePassport(lines: Sequence<String>) = lines
    .flatMap { it.split(' ') }
    .associate { it.split(':').let { (key, value) -> key to value } }
    .let { Passport(it) }
