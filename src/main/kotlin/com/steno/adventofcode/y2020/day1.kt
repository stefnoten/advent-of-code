package com.steno.adventofcode.y2020

import com.steno.Assignment

fun main() {
    Assignment("2020/day1", String::toInt)
        .eval { input ->
            input.toSet()
                .let { it.find { i -> 2020 - i in it } }!!
                .let { it * (2020 - it) }
        }
        .eval { input ->
            input.toSet().let { all ->
                all.firstNotNullOf { i ->
                    all.find { j -> 2020 - i - j in all }
                        ?.let { j -> i * j * (2020 - i - j) }
                }
            }
        }
}
