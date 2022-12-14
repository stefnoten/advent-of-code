package com.steno.adventofcode.util

import kotlin.math.max
import kotlin.math.min

val IntProgression.totalSteps get() = (last - first) / step + 1

infix fun IntRange.intersect(other: IntRange): IntRange = max(first, other.first)..min(last, other.last)

operator fun IntRange.contains(other: IntRange): Boolean = other.first >= first && other.last <= last

fun IntRange.isNotEmpty(): Boolean = !isEmpty()
