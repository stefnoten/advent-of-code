package com.steno.adventofcode.spec

import java.io.File
import kotlin.test.assertEquals

class Spec<R>(
    val file: File,
    val eval: (lines: Sequence<String>) -> R,
) {
    val name = file.nameWithoutExtension

    operator fun invoke() = file.useLines {
        materialize(eval(it))
    }

    fun <R2> map(fn: (R) -> R2) = Spec(file) { fn(eval(it)) }

    fun expect(value: R?) = when (value) {
        null -> this
        else -> map { assertEquals(value, it); it }
    }

    private fun materialize(result: Any?): Any? = when (result) {
        is Sequence<*> -> result.toList()
        else -> result
    }
}
