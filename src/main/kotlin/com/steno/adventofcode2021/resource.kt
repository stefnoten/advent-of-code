package com.steno.adventofcode2021

import java.io.File

private val CONTEXT = object {}.javaClass

fun resourceFile(path: String) = File(CONTEXT.getResource(path)!!.toURI())

fun <T, R> File.useLinesAs(map: (value: String) -> T, fn: (values: Sequence<T>) -> R) = useLines { lines -> fn(lines.map(map)) }

