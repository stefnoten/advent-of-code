package com.steno.adventofcode.util

fun <K, V> Map<K, V>.findKey(predicate: (K, V) -> Boolean) = entries.find { (key, value) -> predicate(key, value) }?.key
