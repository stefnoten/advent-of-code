package com.steno.adventofcode.util

fun <T, R> Sequence<T>.inOrder(block: InOrder<T>.() -> R) = InOrder(this).let(block)

fun <T, R1, R2> Sequence<T>.inOrder(step1: (Sequence<T>) -> R1, step2: (Sequence<T>) -> R2) = inOrder(step1, step2, ::Pair)

fun <T, R1, R2, R> Sequence<T>.inOrder(step1: (Sequence<T>) -> R1, step2: (Sequence<T>) -> R2, transform: (R1, R2) -> R) = iterator().let {
    transform(
        it.asSequence().let(step1),
        it.asSequence().let(step2),
    )
}

fun <T> Sequence<T>.takeWhileNot(predicate: (T) -> Boolean) = takeWhile { !predicate(it) }

fun <T> Sequence<T>.takeUntil(predicate: (T) -> Boolean) = sequence {
    iterator().let {
        while (it.hasNext()) {
            val value = it.next()
            yield(value)
            if (predicate(value)) {
                break
            }
        }
    }
}

fun <T> Sequence<T>.split(limit: Int = 0, predicate: (T) -> Boolean): Sequence<Sequence<T>> = iterator().let { iterator ->
    sequence {
        var count = 0
        while (iterator.hasNext() && (limit == 0 || ++count != limit)) {
            var foundSplitMarker = false
            val subSequence = iterator.asSequence()
                .takeWhileNot { elem -> predicate(elem).also { foundSplitMarker = it } }
            yield(subSequence)
            if (!foundSplitMarker && iterator.hasNext()) {
                iterator.asSequence().takeWhileNot(predicate).count()
            }
        }
        if (iterator.hasNext()) {
            yield(iterator.asSequence())
        }
    }
}

fun <T, K> Sequence<T>.untilStable(property: (T) -> K) = zipWithNext()
    .takeWhileNot { (current, next) -> property(current) == property(next) }
    .map { it.second }

fun <T> Sequence<T>.untilStable() = untilStable { it }

class InOrder<T>(sequence: Sequence<T>): Iterator<T> {
    private val iterator = sequence.iterator()

    override fun hasNext() = iterator.hasNext()
    override fun next() = iterator.next()
    fun <R> next(block: Sequence<T>.() -> R) = block(iterator.asSequence())
}
