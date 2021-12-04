package com.steno

import java.io.File

private val CONTEXT = object {}.javaClass

class Assignment<T> (private val files: List<File>, private val mapLine: (String) -> T) {
    private var nextEvaluation = 1

    fun <R> eval(algorithm: (Sequence<T>) -> R) = this.also {
        println("\u001b[32mPart ${nextEvaluation++}\u001B[0m")
        files
            .map { it.nameWithoutExtension to evalOne(it, algorithm) }
            .let { printResults(it) }
    }

    fun <R> evalList(algorithm: (List<T>) -> R) = eval { algorithm(it.toList()) }

    fun <R> evalSet(algorithm: (Set<T>) -> R) = eval { algorithm(it.toSet()) }

    private fun <R> evalOne(file: File, algorithm: (Sequence<T>) -> R) =
        file.useLines { algorithm(it.map(mapLine)) }

    private fun <T> printResults(results: List<Pair<String, T>>) {
        val titleWidth = results.maxOf { (title, _) -> title.length }
        results.forEach { (title, result) ->
            val paddedTitle = "$title:".padEnd(titleWidth + 1)
            println("\u001b[33m  $paddedTitle \u001B[0m $result")
        }
    }
}

fun <T> assignment(folder: String, mapLine: (String) -> T) = Assignment(
    filesIn(folder).sortedBy { it.name },
    mapLine
)
fun assignment(folder: String) = assignment(folder) { it }

private fun filesIn(folder: String) = resourceFile("/$folder").listFiles()!!

private fun resourceFile(path: String) = File(CONTEXT.getResource(path)!!.toURI())

