package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.spec.mapEach
import com.steno.adventofcode.util.takeCycle

class Day20 : AdventOfCodeSpec({ challenge ->
    val decryptionKey = 811589153L
    challenge.mapEach { it.toLong() }.map { EncryptedFile(it.toList()) }
        .eval(3, 6712) { file ->
            file.mix().groveCoordinates.sum()
        }
        .eval(1623178306, 1595584274798) { file ->
            file.map { it * decryptionKey }.mix(10).groveCoordinates.sum()
        }
}) {
    data class EncryptedFile(val values: List<Long>) {
        val size: Int get() = values.size
        val groveCoordinates get() = listOf(this[1000], this[2000], this[3000])

        operator fun get(index: Long) = values[(index % size).toInt()]

        fun map(fn: (Long) -> Long) = EncryptedFile(values.map { fn(it) })

        fun mix(times: Int = 1): EncryptedFile {
            val list = asLinkedList()
            val zero = list.find { it.value == 0L }!!
            repeat(times) {
                list.forEach { it.move((it.value % (size - 1)).toInt()) }
            }
            return EncryptedFile(zero.cycleForward().map { it.value }.toList())
        }

        private fun asLinkedList() = values
            .map { Node(it) }
            .runningReduce { previous, current ->
                previous.next = current
                current.previous = previous
                current
            }
            .toList().also {
                it.first().previous = it.last()
                it.last().next = it.first()
            }
    }

    class Node(val value: Long) {
        lateinit var previous: Node
        lateinit var next: Node

        fun move(delta: Int) {
            when {
                delta > 0 -> remove().insertAfter(forward().elementAt(delta))
                delta < 0 -> remove().insertBefore(backward().elementAt(-delta))
                else -> Unit
            }
        }

        fun cycleForward() = forward().takeCycle { it }

        fun forward() = generateSequence(this) { it.next }

        fun backward() = generateSequence(this) { it.previous }

        fun remove(): Node {
            previous.next = next
            next.previous = previous
            return this
        }

        fun insertBefore(target: Node): Node {
            insertAfter(target.previous)
            return this
        }

        fun insertAfter(target: Node): Node {
            previous = target
            next = target.next
            target.next.previous = this
            target.next = this
            return this
        }
    }
}
