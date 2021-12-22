package com.steno.adventofcode.util

fun <T> cycle(values: Iterable<T>) = sequence {
    while (true) {
        yieldAll(values)
    }
}
