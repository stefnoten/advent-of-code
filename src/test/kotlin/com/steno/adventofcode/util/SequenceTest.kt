package com.steno.adventofcode.util

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertFails
import kotlin.test.expect

class SequenceTest {
    @Nested
    inner class Split {
        @Test
        fun returnsCorrectItems() {
            expect(
                listOf(listOf(1, 2), listOf(4, 5), listOf(7, 8), listOf(10))
            ) {
                (1..10).asSequence()
                    .split { it % 3 == 0 }
                    .map { it.toList() }
                    .toList()
            }
        }

        @Test
        fun givenNotSurroundedByItems_returnsCorrectItemsAndIgnoresLast() {
            val splitAt = setOf(1, 4, 5, 10)
            expect(
                listOf(listOf(), listOf(2, 3), listOf(), listOf(6, 7, 8, 9))
            ) {
                (1..10).asSequence()
                    .split { it in splitAt }
                    .map { it.toList() }
                    .toList()
            }
        }

        @Test
        fun givenNoSplit_returnsCorrectItems() {
            expect(
                listOf(listOf(1, 2, 3))
            ) {
                (1..3).asSequence()
                    .split { false }
                    .map { it.toList() }
                    .toList()
            }
        }

        @Test
        fun givenDroppedEntries_returnsCorrectItems() {
            expect(
                listOf(listOf(4, 5), listOf(10))
            ) {
                (1..10).asSequence()
                    .split { it % 3 == 0 }
                    .filterIndexed { i, _ -> i % 2 == 1 }
                    .map { it.toList() }
                    .toList()
            }
        }

        @Test
        fun givenConsumedTooLate_fails() {
            assertFails {
                (1..10).asSequence()
                    .split { it % 3 == 0 }
                    .toList()
                    .map { it.toList() }
            }
        }

        @Test
        fun givenPartiallyConsumed_returnsCorrectItems() {
            expect(
                listOf(listOf(1), listOf(4), listOf(7), listOf(10))
            ) {
                (1..10).asSequence()
                    .split { it % 3 == 0 }
                    .map { it.take(1).toList() }
                    .toList()
            }
        }

        @Test
        fun givenLimit_hasCorrectItems() {
            expect(
                listOf(listOf(1, 2), listOf(4, 5, 6, 7, 8, 9, 10))
            ) {
                (1..10).asSequence()
                    .split(limit = 2) { it % 3 == 0 }
                    .map { it.toList() }
                    .toList()
            }
        }
    }

    @Nested
    inner class GenerateSequenceNested {
        @Test
        fun returnsCorrectItems() {
            expect(
                listOf(listOf(1), listOf(1, 2), listOf(1, 2, 3))
            ) {
                generateSequenceNested(sequenceOf(1)) { (1..(it + 1)).asSequence() }
                    .map { it.toList() }
                    .take(3)
                    .toList()
            }
        }

        @Test
        fun givenDroppedEntries_returnsCorrectItems() {
            expect(
                listOf(listOf(1, 2), listOf(1, 2, 3, 4), listOf(1, 2, 3, 4, 5, 6))
            ) {
                generateSequenceNested(sequenceOf(1)) { (1..(it + 1)).asSequence() }
                    .filterIndexed { i, _ -> i % 2 == 1 }
                    .map { it.toList() }
                    .take(3)
                    .toList()
            }
        }
    }

    @Nested
    inner class FlatScan {
        @Test
        fun returnsCorrectItems() {
            expect(
                listOf(listOf(1), 1..(1 + 1), 1..(2 + 2), 1..(3 + 4)).flatten()
            ) {
                sequenceOf(1, 2, 3)
                    .flatScan(1) { acc, n ->
                        (1..(n + acc)).asSequence()
                    }.toList()
            }
        }
    }
}
