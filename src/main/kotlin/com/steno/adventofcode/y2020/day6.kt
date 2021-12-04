package com.steno.adventofcode.y2020

import com.steno.adventofcode.util.split
import com.steno.assignment

private fun main() {
    assignment("2020/day6") { lines -> lines.split { it.isEmpty() }.map { it.toList() } }
        .eval { groups ->
            groups
                .sumOf { it.joinToString("").toSet().count() }
        }
        .eval { groups ->
            groups
                .sumOf { group ->
                    ('a'..'z').count { question ->
                        group.all { question in it }
                    }
                }
        }
}
