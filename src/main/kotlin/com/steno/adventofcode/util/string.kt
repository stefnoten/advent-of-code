package com.steno.adventofcode.util

fun String.toDigits() = toDigits { it }
fun <R> String.toDigits(fn: (Int) -> R) = toCharArray().map { i -> fn(i.digitToInt()) }

fun Sequence<String>.toDigits() = toDigits { it }
fun <R> Sequence<String>.toDigits(fn: (Int) -> R) = map { it.toDigits(fn) }

fun String.color(color: AnsiColor) = "\u001b[${color.value}m${this}\u001B[0m"

enum class AnsiColor(val value: Int) {
    BLACK(30),
    RED(31),
    GREEN(32),
    YELLOW(33),
    BLUE(34),
    MAGENTA(35),
    CYAN(36),
    WHITE(37)
}
