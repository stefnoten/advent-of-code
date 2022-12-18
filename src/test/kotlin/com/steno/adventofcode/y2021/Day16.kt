package com.steno.adventofcode.y2021

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.inOrder
import com.steno.adventofcode.util.takeUntil

private class Day16 : AdventOfCodeSpec({ challenge ->
    challenge.map { it.first() }
        .eval(6, 9, 14, 16, 12, 23, 31, 14, 8, 15, 11, 13, 19, 16, 20, 986) {
            print("$it -> ")
            Packet.fromString(it).versionCount
        }
        .eval(2021, 1, 3, 15, 46, 46, 54, 3, 54, 7, 9, 1, 0, 0, 1, 18234816469452) {
            print("$it -> ")
            Packet.fromString(it).value
        }
        .eval { Packet.fromString(it) }
}) {
    sealed class Packet {
        abstract val value: Long
        abstract val versionCount: Int

        companion object {
            fun fromString(line: String) = line.asSequence()
                .map { it.digitToInt(16) }
                .let { fromBits(it.asSequence().bits()) }

            fun fromBits(bits: Sequence<Int>): Packet = bits.inOrder {
                val version = next { take(3).digitsToInt() }
                when (val type = next { take(3).digitsToInt() }) {
                    4 -> LiteralPacket.parse(version, asSequence())
                    else -> OperatorPacket.parse(type, version, asSequence())
                }
            }

            fun allFromBits(bits: Sequence<Int>) = bits.chunked { fromBits(it.asSequence()) }
        }
    }

    data class LiteralPacket(val version: Int, override val value: Long) : Packet() {
        override val versionCount: Int
            get() = version

        override fun toString() = "$value"

        companion object {
            fun parse(version: Int, bits: Sequence<Int>) = LiteralPacket(
                version,
                bits.chunked(5)
                    .takeUntil { it.first() == 0 }
                    .map { it.drop(1).asSequence().digitsToInt() }
                    .digitsToLong(4)
            )
        }
    }

    sealed class OperatorPacket(val version: Int, val packets: List<Packet>) : Packet() {
        override val versionCount: Int
            get() = version + packets.sumOf { it.versionCount }

        companion object {
            fun parse(type: Int, version: Int, bits: Sequence<Int>): Packet {
                val subPackets = bits.inOrder {
                    when (next { first() }) {
                        0 -> next { take(15).digitsToInt() }
                            .let { bitCount -> next { take(bitCount) } }
                            .let { bits -> allFromBits(bits).toList() }

                        else -> next { take(11).digitsToInt() }
                            .let { packetCount -> allFromBits(asSequence()).take(packetCount).toList() }
                    }
                }
                return when (type) {
                    0 -> SumPacket(version, subPackets)
                    1 -> ProductPacket(version, subPackets)
                    2 -> MinPacket(version, subPackets)
                    3 -> MaxPacket(version, subPackets)
                    5 -> GreaterThanPacket(version, subPackets)
                    6 -> LessThanPacket(version, subPackets)
                    7 -> EqualToPacket(version, subPackets)
                    else -> throw IllegalStateException()
                }
            }
        }
    }

    class SumPacket(version: Int, packets: List<Packet>) : OperatorPacket(version, packets) {
        override val value: Long
            get() = packets.sumOf { it.value }

        override fun toString() = "[${packets.joinToString("+")}=$value]"
    }

    class ProductPacket(version: Int, packets: List<Packet>) : OperatorPacket(version, packets) {
        override val value: Long
            get() = packets.map { it.value }.reduce(Long::times)

        override fun toString() = "[${packets.joinToString("*")}=$value]"
    }

    class MinPacket(version: Int, packets: List<Packet>) : OperatorPacket(version, packets) {
        override val value: Long
            get() = packets.map { it.value }.reduce(::minOf)

        override fun toString() = "[min(${packets.joinToString(", ")})=$value]"
    }

    class MaxPacket(version: Int, packets: List<Packet>) : OperatorPacket(version, packets) {
        override val value: Long
            get() = packets.map { it.value }.reduce(::maxOf)

        override fun toString() = "[max(${packets.joinToString(", ")})=$value]"
    }

    class GreaterThanPacket(version: Int, packets: List<Packet>) : OperatorPacket(version, packets) {
        override val value: Long
            get() = packets.map { it.value }.let { (a, b) -> if (a > b) 1 else 0 }

        override fun toString() = "[${packets.joinToString(">")}=$value]"
    }

    class LessThanPacket(version: Int, packets: List<Packet>) : OperatorPacket(version, packets) {
        override val value: Long
            get() = packets.map { it.value }.let { (a, b) -> if (a < b) 1 else 0 }

        override fun toString() = "[${packets.joinToString("<")}=$value]"
    }

    class EqualToPacket(version: Int, packets: List<Packet>) : OperatorPacket(version, packets) {
        override val value: Long
            get() = packets.map { it.value }.let { (a, b) -> if (a == b) 1 else 0 }

        override fun toString() = "[${packets.joinToString("==")}=$value]"
    }

    companion object {
        fun Sequence<Int>.digitsToInt(bitsPerDigit: Int = 1) = fold(0) { acc, i -> (acc shl bitsPerDigit) + i }
        fun Sequence<Int>.digitsToLong(bitsPerDigit: Int = 1) = fold(0L) { acc, i -> (acc shl bitsPerDigit) + i }
        fun Sequence<Int>.bits() = flatMap { sequenceOf(it.bitAt(3), it.bitAt(2), it.bitAt(1), it.bitAt(0)) }
        fun Int.bitAt(i: Int) = toInt() shr i and 1

        fun <T, R> Sequence<T>.chunked(fn: (Iterator<T>) -> R) = iterator().let {
            sequence {
                while (it.hasNext()) {
                    yield(fn(it))
                }
            }
        }
    }
}
