package com.steno.adventofcode.util

fun String.toDigits() = toDigits { it }
fun <R> String.toDigits(fn: (Int) -> R) = toCharArray().map { i -> fn(i.digitToInt()) }

fun Sequence<String>.toDigits() = toDigits { it }
fun <R> Sequence<String>.toDigits(fn: (Int) -> R) = map { it.toDigits(fn) }

fun String.color(color: Int) = "\u001b[${color}m${this}\u001B[0m"
