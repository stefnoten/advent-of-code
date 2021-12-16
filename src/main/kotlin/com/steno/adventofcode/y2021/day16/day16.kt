package com.steno.adventofcode.y2021.day16

import com.steno.adventofcode.util.inOrder
import com.steno.adventofcode.util.takeUntil
import com.steno.assignment

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

private fun <T, R> Sequence<T>.chunked(fn: (Iterator<T>) -> R) = iterator().let {
    sequence {
        while (it.hasNext()) {
            yield(fn(it))
        }
    }
}

private fun Sequence<Int>.digitsToInt(bitsPerDigit: Int = 1) = fold(0) { acc, i -> (acc shl bitsPerDigit) + i }
private fun Sequence<Int>.digitsToLong(bitsPerDigit: Int = 1) = fold(0L) { acc, i -> (acc shl bitsPerDigit) + i }
private fun Sequence<Int>.bits() = flatMap { sequenceOf(it.bitAt(3), it.bitAt(2), it.bitAt(1), it.bitAt(0)) }
private fun Int.bitAt(i: Int) = toInt() shr i and 1

private fun main() {
    assignment("2021/day16") { it.first() }
        .eval { "$it -> " + Packet.fromString(it).versionCount }
        .eval { "$it -> " + Packet.fromString(it).value }
        .eval { Packet.fromString(it) }
}
