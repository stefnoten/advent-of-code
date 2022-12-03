package com.steno.adventofcode.spec

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestFactory
import java.io.File

abstract class AdventOfCodeSpec(
    private val spec: Input.() -> Challenge<*>
) {
    private val year = javaClass.packageName.substringAfterLast(".y")
    private val resourcePath = "$year/${javaClass.simpleName.lowercase()}"
    private val files = filesIn(this.resourcePath)

    @TestFactory
    @DisplayName("Parts")
    fun tests() = spec(Input(files)).tests

}

class Input(private val files: List<File>) {
    fun <T> parseAllLines(parse: (lines: Sequence<String>) -> T) = Challenge(files) { parse(it) }
    fun <T> parsePerLine(parse: (line: String) -> T) = parseAllLines { it.map(parse) }

    val lines = challenge(files)
}

private val CONTEXT = object {}.javaClass
private fun filesIn(folder: String) = resourceFile("/$folder").listFiles()!!.sortedBy { it.name }
private fun resourceFile(path: String) = File(CONTEXT.getResource(path)!!.toURI())
