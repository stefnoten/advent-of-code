package com.steno.adventofcode.util

fun <T, R1, R2> Sequence<T>.inOrder(step1: (Sequence<T>) -> R1, step2: (Sequence<T>) -> R2) = inOrder(step1, step2, ::Pair)

fun <T, R1, R2, R> Sequence<T>.inOrder(step1: (Sequence<T>) -> R1, step2: (Sequence<T>) -> R2, transform: (R1, R2) -> R) = iterator().let {
    transform(
        it.asSequence().let(step1),
        it.asSequence().let(step2),
    )
}
