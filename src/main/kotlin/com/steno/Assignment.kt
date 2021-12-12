package com.steno

import java.io.File

private val CONTEXT = object {}.javaClass

data class Assignment<T>(
    private val files: List<File>,
    private val evaluation: Int = 1,
    private val parse: (lines: Sequence<String>) -> T
) {
    fun focusOn(fileName: String) = copy(files = files.filter { it.nameWithoutExtension == fileName })

    fun <R> map(fn: (T) -> R) = Assignment(files, evaluation) { parse(it).let(fn) }

    fun <R> eval(algorithm: (T) -> R): Assignment<T> {
        println("\u001b[32mPart ${evaluation}\u001B[0m")
        files
            .map { it.nameWithoutExtension to evalOne(it, algorithm) }
            .let { printResults(it) }
        return copy(evaluation = evaluation + 1)
    }

    private fun <R> evalOne(file: File, algorithm: (T) -> R) =
        file.useLines { parse(it).let(algorithm) }

    private fun <T> printResults(results: List<Pair<String, T>>) {
        val titleWidth = results.maxOf { (title, _) -> title.length }
        results.forEach { (title, result) ->
            val paddedTitle = "$title:".padEnd(titleWidth + 1)
            println("\u001b[33m  $paddedTitle \u001B[0m ${printable(result)}")
        }
    }

    private fun <T> printable(result: T): Any? = when (result) {
        is Sequence<*> -> result.toList().joinToString("\n")
        is Collection<*> -> result.joinToString("\n")
        else -> result
    }
}

fun <T, R> Assignment<Sequence<T>>.evalList(algorithm: (List<T>) -> R) = eval { algorithm(it.toList()) }

fun <T> assignment(folder: String, mapLines: (Sequence<String>) -> T) = Assignment(filesIn(folder)) { mapLines(it) }
fun assignment(folder: String) = assignment(folder) { it }

private fun filesIn(folder: String) = resourceFile("/$folder").listFiles()!!.sortedBy { it.name }

private fun resourceFile(path: String) = File(CONTEXT.getResource(path)!!.toURI())

