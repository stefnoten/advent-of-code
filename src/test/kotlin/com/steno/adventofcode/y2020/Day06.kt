package com.steno.adventofcode.y2020

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.split

private class Day06: AdventOfCodeSpec({ challenge ->
    challenge.map { lines -> lines.split { it.isEmpty() }.map { it.toList() } }
        .eval(11, 6583) { groups ->
            groups
                .sumOf { it.joinToString("").toSet().count() }
        }
        .eval(6, 3290) { groups ->
            groups
                .sumOf { group ->
                    ('a'..'z').count { question ->
                        group.all { question in it }
                    }
                }
        }
})
