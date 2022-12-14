package com.steno.adventofcode.spec

import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import java.io.File
import kotlin.test.assertEquals

data class Challenge<T>(
    private val files: List<File>,
    private val evaluation: Int = 1,
    internal val tests: List<DynamicNode> = listOf(),
    private val parse: (lines: Sequence<String>) -> T
) {
    fun focusOn(fileName: String) = copy(files = files.filter { it.nameWithoutExtension == fileName })
    fun skipRemaining() = copy(files = emptyList())

    fun <R> map(fn: (T) -> R) = Challenge(files, evaluation, tests) { parse(it).let(fn) }

    fun <R> eval(vararg expected: R, algorithm: (T) -> R) = copy(
        evaluation = evaluation + 1,
        tests = tests + specPerFile(expected.toList(), algorithm)
    )

    private fun <R> specPerFile(expected: List<R>, algorithm: (T) -> R) = dynamicContainer(
        "Part $evaluation",
        files.mapIndexed { index, file ->
            val title = file.nameWithoutExtension
            val expectedValue = expected.getOrNull(index)
            dynamicTest(title) {
                val result = evalFile(file) {
                    materialize(algorithm(it))
                }
                print("Part $evaluation: ".color(32))
                val paddedTitle = "${title}: ".padEnd(files.maxOf { it.nameWithoutExtension.length + 2 })
                print(paddedTitle.color(33))
                println(printable(result).let { if ("\n" in it) "\n" + it else it })
                expectedValue?.let { assertEquals(it, result) }
            }
        }
    )

    private fun String.color(color: Int) = "\u001b[${color}m${this}\u001B[0m"

    private fun <R> evalFile(file: File, algorithm: (T) -> R) =
        file.useLines { algorithm(parse(it)) }

    private fun materialize(result: Any?): Any? = when (result) {
        is Sequence<*> -> result.toList()
        else -> result
    }
    private fun <T> printable(result: T): String = when (result) {
        is Iterable<*> -> result.joinToString("\n")
        else -> result.toString()
    }
}

fun challenge(files: List<File>) = Challenge(files) { it }

fun <T, R> Challenge<Sequence<T>>.mapEach(map: (value: T) -> R) = map { it.map(map) }
