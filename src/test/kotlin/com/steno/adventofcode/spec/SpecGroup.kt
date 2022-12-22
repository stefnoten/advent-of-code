package com.steno.adventofcode.spec

import com.steno.adventofcode.util.AnsiColor.GREEN
import com.steno.adventofcode.util.AnsiColor.YELLOW
import com.steno.adventofcode.util.color
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest

data class SpecGroup<R>(val name: String, val specs: List<Spec<R>>) {
    private val longestSpecName = specs.maxOfOrNull { it.name.length } ?: 0

    fun asTests(): DynamicNode = DynamicContainer.dynamicContainer(
        name,
        specs.map { spec ->
            DynamicTest.dynamicTest(spec.name) {
                print(("$name: ").color(GREEN))
                print(
                    "${spec.name}: "
                        .padEnd(longestSpecName + 2)
                        .color(YELLOW)
                )
                val result = spec()
                println(printable(result).let { if ("\n" in it) "\n" + it else it })
            }
        }
    )

    fun expect(expected: List<R>) = copy(specs = specs.mapIndexed { index, spec ->
        spec.expect(expected.getOrNull(index))
    })

    private fun <T> printable(result: T): String = when (result) {
        is Iterable<*> -> result.joinToString("\n")
        else -> result.toString()
    }
}
