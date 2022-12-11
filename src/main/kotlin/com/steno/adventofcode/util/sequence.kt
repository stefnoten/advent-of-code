package com.steno.adventofcode.util

fun <T> Sequence<T>.onFirst(action: (T) -> Unit): Sequence<T> {
    var currentAction = action
    return onEach {
        currentAction(it)
        currentAction = { }
    }
}

fun <T, K> Sequence<T>.onChange(property: (T) -> K, action: (T) -> Unit): Sequence<T> {
    var last: K? = null
    return onEach {
        val next = property(it)
        if (next != last) {
            last = next
            action(it)
        }
    }
}

fun <T, R> Sequence<T>.inOrder(block: InOrder<T>.() -> R) = InOrder(this).let(block)

fun <T, R1, R2> Sequence<T>.inOrder(step1: (Sequence<T>) -> R1, step2: (Sequence<T>) -> R2) = inOrder(step1, step2, ::Pair)

fun <T, R1, R2, R> Sequence<T>.inOrder(step1: (Sequence<T>) -> R1, step2: (Sequence<T>) -> R2, transform: (R1, R2) -> R) = iterator().let {
    transform(
        it.asSequence().let(step1),
        it.asSequence().let(step2),
    )
}

fun <T> Sequence<T>.asSupplier() = iterator()::next

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

fun <T, K: Any> Sequence<T>.takeCycle(property: (T) -> K): Sequence<T> {
    var initial: K? = null
    var changed = false
    return takeUntil {
        val current = property(it)
        when {
            initial == null -> initial = current
            current != initial -> changed = true
        }
        changed && current == initial
    }
}


fun <T> Sequence<T>.split(limit: Int = 0, predicate: (T) -> Boolean): Sequence<Sequence<T>> = iterator().let { iterator ->
    sequence {
        var count = 0
        var completed = false
        while (iterator.hasNext() && (limit == 0 || ++count != limit)) {
            var foundSplitMarker = false
            val subSequence = iterator.asSequence()
                .takeWhileNot { elem -> predicate(elem).also { foundSplitMarker = it } }
                .availableUntil { completed }
            yield(subSequence)
            if (!foundSplitMarker && iterator.hasNext()) {
                iterator.asSequence().takeWhileNot(predicate).forEach { }
            }
        }
        if (iterator.hasNext()) {
            yield(iterator.asSequence())
        }
        completed = true
    }
}

fun <T> Sequence<T>.availableUntil(test: () -> Boolean) = Sequence {
    if (test()) {
        throw IllegalStateException("Sequence is not available anymore")
    }
    iterator()
}

fun <T, K> Sequence<T>.untilStable(property: (T) -> K) = zipWithNext()
    .takeWhileNot { (current, next) -> property(current) == property(next) }
    .map { it.second }

fun <T> Sequence<T>.untilStable() = untilStable { it }

fun <T : Any> generateSequenceNested(seed: Sequence<T>, next: (T) -> Sequence<T>): Sequence<Sequence<T>> = sequence {
    var last: T? = null
    yieldRequiringConsume(seed.onEach { last = it })
    while (last != null) {
        yieldRequiringConsume(next(last!!)
            .also { last = null }
            .onEach { last = it })
    }
}

private suspend fun <T> SequenceScope<Sequence<T>>.yieldRequiringConsume(sequence: Sequence<T>) {
    var consumed = false
    val toYield = sequence.onEach { consumed = true }
    yield(toYield)
    if (!consumed)
        toYield.last()
}

class InOrder<T>(sequence: Sequence<T>) : Iterator<T> {
    private val iterator = sequence.iterator()

    override fun hasNext() = iterator.hasNext()
    override fun next() = iterator.next()
    fun nextExpect(value: T) = next().also { if (it != value) throw NullPointerException("Expected \"$value\" but got \"$it\"") }
    fun <R> next(block: Sequence<T>.() -> R) = block(iterator.asSequence())
}
