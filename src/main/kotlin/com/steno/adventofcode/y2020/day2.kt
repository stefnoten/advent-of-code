package com.steno.adventofcode.y2020

import com.steno.assignment

private data class Input(val i: Int, val j: Int, val char: Char, val password: String)

fun main() {
    assignment("2020/day2", ::parse)
        .eval { lines ->
            lines.count { (min, max, char, password) -> password.count { it == char } in min..max }
        }
        .eval { lines ->
            lines.count { (i, j, char, password) -> "${password[i - 1]}${password[j - 1]}".count { it == char } == 1 }
        }
}

private val FORMAT = Regex("""(\d+)-(\d+) (.): (.*)""")

private fun parse(line: String) = FORMAT.find(line)!!
    .groupValues.drop(1)
    .let { (i, j, char, password) -> Input(i.toInt(), j.toInt(), char[0], password) }
