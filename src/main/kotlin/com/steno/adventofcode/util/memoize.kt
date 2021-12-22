package com.steno.adventofcode.util

import kotlin.reflect.KProperty

fun <T, R> memoize(fn: (T) -> R): Memoized<(T) -> R> = mutableMapOf<T, R>().let { cache ->
    Memoized(fn) { p1 -> cache.getOrPut(p1) { fn(p1) } }
}

fun <T1, T2, R> memoize(fn: (T1, T2) -> R): Memoized<(T1, T2) -> R> = mutableMapOf<Pair<T1, T2>, R>().let { cache ->
    Memoized(fn) { p1, p2 -> cache.getOrPut(Pair(p1, p2)) { fn(p1, p2) } }
}

fun <T1, T2, T3, R> memoize(fn: (T1, T2, T3) -> R): Memoized<(T1, T2, T3) -> R> = mutableMapOf<Triple<T1, T2, T3>, R>().let { cache ->
    Memoized(fn) { p1, p2, p3 -> cache.getOrPut(Triple(p1, p2, p3)) { fn(p1, p2, p3) } }
}

class Memoized<F>(delegate: F, val value: F) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
}
