package com.steno.adventofcode.spec

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestFactory
import java.io.File

abstract class AdventOfCodeSpec(
    private val spec: (Challenge<Sequence<String>>) -> Challenge<*>
) {
    private val year = javaClass.packageName.substringAfterLast(".y")
    private val resourcePath = "$year/${javaClass.simpleName.lowercase()}"
    private val files = filesIn(this.resourcePath)

    @TestFactory
    @DisplayName("Parts")
    fun tests() = spec(challenge(files)).asTests()

}

private val CONTEXT = object {}.javaClass
private fun filesIn(folder: String) = resourceFile("/$folder").listFiles()!!.sortedBy { it.name }
private fun resourceFile(path: String) = File(CONTEXT.getResource(path)!!.toURI())
