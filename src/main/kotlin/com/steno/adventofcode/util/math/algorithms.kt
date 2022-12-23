package com.steno.adventofcode.util.math

fun gcd(a: Int, b: Int): Int = when {
    a > b -> gcd(a - b, b)
    a < b -> gcd(b - a, a)
    else -> a
}
