package com.steno.adventofcode.util

fun <T, R1, R2> Sequence<T>.inOrder(step1: (Sequence<T>) -> R1, step2: (Sequence<T>) -> R2) = inOrder(step1, step2, ::Pair)

fun <T, R1, R2, R> Sequence<T>.inOrder(step1: (Sequence<T>) -> R1, step2: (Sequence<T>) -> R2, transform: (R1, R2) -> R) = iterator().let {
    transform(
        it.asSequence().let(step1),
        it.asSequence().let(step2),
    )
}

fun <T> Sequence<T>.takeWhileNot(predicate: (T) -> Boolean) = takeWhile { !predicate(it) }

fun <T> Sequence<T>.split(limit: Int = 0, predicate: (T) -> Boolean): Sequence<Sequence<T>> = iterator().let { iterator ->
    sequence {
        var count = 0
        do {
            if (limit != 0 && ++count == limit) {
                yield(iterator.asSequence())
            } else {
                var foundSplitMarker = false
                val subSequence = iterator.asSequence()
                    .takeWhileNot { elem -> predicate(elem).also { foundSplitMarker = it } }
                yield(subSequence)
                if (!foundSplitMarker && iterator.hasNext()) {
                    iterator.asSequence().takeWhileNot(predicate).count()
                }
            }
        } while (iterator.hasNext())
    }
}
