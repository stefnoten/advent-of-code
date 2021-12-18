package com.steno.adventofcode.util

fun <R> Regex.parse(text: String, destructure: (MatchResult.Destructured) -> R) =
    matchEntire(text)?.destructured?.let(destructure)
        ?: throw NullPointerException("\"$text\" does not match $this")
