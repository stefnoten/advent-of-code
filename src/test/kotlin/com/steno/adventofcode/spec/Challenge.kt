package com.steno.adventofcode.spec

import java.io.File

data class Challenge<T>(
    private val files: List<File>,
    private val evaluation: Int = 1,
    private val specs: List<SpecGroup<*>> = listOf(),
    private val parse: (lines: Sequence<String>) -> T
) {
    fun focusOn(fileName: String) = copy(files = files.filter { it.nameWithoutExtension == fileName })
    fun skipRemaining() = copy(files = emptyList())

    fun <R> map(fn: (T) -> R) = Challenge(files, evaluation, specs) { parse(it).let(fn) }

    fun <R> eval(vararg expected: R, algorithm: (T) -> R) = copy(
        evaluation = evaluation + 1,
        specs = specs + SpecGroup(
            "Part $evaluation",
            files.map { file -> Spec(file) { algorithm(parse(it)) } }
        ).expect(expected.toList())
    )

    internal fun asTests() = specs.map { it.asTests() }

}

fun challenge(files: List<File>) = Challenge(files) { it }

fun <T, R> Challenge<Sequence<T>>.mapEach(map: (value: T) -> R) = map { it.map(map) }
