package com.steno.adventofcode.util

fun String.toDigits() = toDigits { it }
fun <R> String.toDigits(fn: (Int) -> R) = toCharArray().map { i -> fn(i.digitToInt()) }

fun Sequence<String>.toDigits() = toDigits { it }
fun <R> Sequence<String>.toDigits(fn: (Int) -> R) = map { it.toDigits(fn) }
