package com.steno.adventofcode.y2020

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.spec.mapEach

private class Day02: AdventOfCodeSpec({ challenge ->
    challenge.mapEach { parseLine(it) }
        .eval(2, 500) { lines ->
            lines.count { (min, max, char, password) -> password.count { it == char } in min..max }
        }
        .eval(1, 313) { lines ->
            lines.count { (i, j, char, password) -> "${password[i - 1]}${password[j - 1]}".count { it == char } == 1 }
        }
}) {
    data class Input(val i: Int, val j: Int, val char: Char, val password: String)

    companion object {
        val FORMAT = Regex("""(\d+)-(\d+) (.): (.*)""")

        fun parseLine(line: String) = FORMAT.find(line)!!
            .groupValues.drop(1)
            .let { (i, j, char, password) -> Input(i.toInt(), j.toInt(), char[0], password) }

    }
}
