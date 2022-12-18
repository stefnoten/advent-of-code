package com.steno.adventofcode.util.math

interface VectorRange<T: Vector<T>>: Iterable<T> {
    val min: T
    val max: T
    operator fun contains(vector: T): Boolean
}
