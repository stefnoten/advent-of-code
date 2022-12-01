package com.steno.adventofcode.spec

import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import java.io.File

data class Challenge<T>(
    private val files: List<File>,
    private val evaluation: Int = 1,
    internal val tests: List<DynamicNode> = listOf(),
    private val parse: (lines: Sequence<String>) -> T
) {
    fun focusOn(fileName: String) = copy(files = files.filter { it.nameWithoutExtension == fileName })
    fun skipRemaining() = copy(files = emptyList())

    fun <R> map(fn: (T) -> R) = Challenge(files, evaluation, tests) { parse(it).let(fn) }

    fun <R> eval(algorithm: (T) -> R) = copy(
        evaluation = evaluation + 1,
        tests = tests + specPerFile(algorithm)
    )

    private fun <R> specPerFile(algorithm: (T) -> R) = dynamicContainer(
        "Part $evaluation",
        files.map { file ->
            val title = file.nameWithoutExtension
            dynamicTest(title) {
                val result = evalFile(file, algorithm)
                print("Part $evaluation: ".color(32))
                val paddedTitle = "${title}: ".padEnd(files.maxOf { it.nameWithoutExtension.length + 2 })
                print(paddedTitle.color(33))
                println(printable(result))
            }
        }
    )

    private fun String.color(color: Int) = "\u001b[${color}m${this}\u001B[0m"

    private fun <R> evalFile(file: File, algorithm: (T) -> R) =
        file.useLines { algorithm(parse(it)) }

    private fun <T> printable(result: T): Any? = when (result) {
        is Sequence<*> -> result.toList().joinToString("\n")
        is Collection<*> -> result.joinToString("\n")
        else -> result
    }
}

fun challenge(files: List<File>) = Challenge(files) { it }
