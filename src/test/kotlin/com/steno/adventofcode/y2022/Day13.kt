package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.y2022.Day13.Packet.Companion.toPacket
import java.util.*

class Day13 : AdventOfCodeSpec({ challenge ->
    challenge.map { lines -> lines.filter { it.isNotEmpty() }.map { it.toPacket() } }
        .eval(13, 5196) { packets ->
            packets
                .chunked(2)
                .withIndex()
                .filter { it.value.let { (a, b) -> a <= b } }
                .sumOf { it.index + 1 }
        }
        .eval(140, 22134) { packets ->
            val dividers = setOf("[[2]]".toPacket(), "[[6]]".toPacket())
            (packets + dividers)
                .sorted()
                .withIndex()
                .filter { it.value in dividers }
                .map { it.index + 1 }
                .reduce(Int::times)
        }
}) {
    sealed interface Packet : Comparable<Packet> {
        fun asList(): Node

        companion object {
            fun String.toPacket() = parse(tokens(this))!!

            private fun parse(tokens: Iterator<String>): Packet? =
                when (val first = tokens.next()) {
                    "[" -> Node(
                        sequence<Packet> {
                            var entry = parse(tokens)
                            while (entry != null) {
                                yield(entry)
                                entry = when (tokens.next()) {
                                    "," -> parse(tokens)
                                    "]" -> null
                                    else -> throw IllegalStateException()
                                }
                            }
                        }.toList()
                    )
                    "]" -> null
                    else -> Leaf(first.toInt())
                }

            private fun tokens(line: String) = StringTokenizer(line, "[,]", true)
                .asSequence()
                .map { it as String }
                .iterator()
        }
    }

    data class Node(val values: List<Packet>) : Packet {
        override fun asList() = this
        override fun compareTo(other: Packet): Int {
            val otherValues = other.asList().values
            return values.asSequence().zip(otherValues.asSequence()) { a, b -> a compareTo b }
                .firstOrNull { it != 0 }
                ?: (values.size compareTo otherValues.size)
        }
        override fun toString() = "$values"
    }

    data class Leaf(val value: Int) : Packet {
        override fun asList() = Node(listOf(this))
        override fun compareTo(other: Packet) = when (other) {
            is Leaf -> value compareTo other.value
            is Node -> asList() compareTo other
        }
        override fun toString() = "$value"
    }
}
