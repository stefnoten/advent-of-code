package com.steno.adventofcode.y2021

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.spec.mapEach
import com.steno.adventofcode.util.inOrder

private class Day18: AdventOfCodeSpec({ challenge ->
    challenge.mapEach { parseTree(it) }
        .eval(4140, 4145) { trees -> trees.reduce(Tree::plus).magnitude }
        .map { it.toList() }
        .eval(3993, 4855) { trees ->
            trees.maxOf { first ->
                trees.maxOf { second -> (first + second).magnitude }
            }
        }
}) {
    sealed class Tree {
        abstract val depth: Int
        abstract val magnitude: Int

        abstract fun addLeft(value: Int): Tree
        abstract fun addRight(value: Int): Tree
        abstract fun explode(maxDepth: Int = 4): Explosion?
        abstract fun split(): Split?
        fun reduce(): Tree = (explode()?.result ?: split()?.result)?.reduce()
            ?: this

        operator fun plus(other: Tree) = Node(this, other).reduce()
    }

    data class Leaf(val value: Int) : Tree() {
        override val depth = 0
        override val magnitude: Int
            get() = value

        override fun addLeft(value: Int) = Leaf(this.value + value)
        override fun addRight(value: Int) = Leaf(this.value + value)
        override fun explode(maxDepth: Int): Explosion? = null
        override fun split() = when (value >= 10) {
            true -> Split(Node(Leaf(value / 2), Leaf((value + 1) / 2)))
            false -> null
        }

        override fun toString() = value.toString()
    }

    data class Node(val left: Tree, val right: Tree) : Tree() {
        override val depth = maxOf(left.depth, right.depth) + 1
        override val magnitude: Int
            get() = 3 * left.magnitude + 2 * right.magnitude

        override fun addLeft(value: Int) = copy(left = left.addLeft(value))
        override fun addRight(value: Int) = copy(right = right.addRight(value))
        override fun explode(maxDepth: Int) = when {
            depth <= maxDepth -> null
            left is Leaf && right is Leaf -> Explosion(Leaf(0), left.value, right.value)
            else -> explodeLeft(maxDepth - 1) ?: explodeRight(maxDepth - 1)
        }

        override fun split() = left.split()?.let { it.copy(result = copy(left = it.result)) }
            ?: right.split()?.let { it.copy(result = copy(right = it.result)) }

        private fun explodeLeft(maxDepth: Int) = left.explode(maxDepth)
            ?.let { explosion ->
                explosion.copy(
                    damageRight = 0,
                    result = copy(
                        left = explosion.result,
                        right = right.addLeft(explosion.damageRight)
                    ),
                )
            }

        private fun explodeRight(maxDepth: Int) = right.explode(maxDepth)
            ?.let { explosion ->
                explosion.copy(
                    damageLeft = 0,
                    result = copy(
                        left = left.addRight(explosion.damageLeft),
                        right = explosion.result,
                    ),
                )
            }

        override fun toString() = "[$left,$right]"
    }

    data class Explosion(val result: Tree, val damageLeft: Int, val damageRight: Int)
    data class Split(val result: Tree)

    companion object {
        fun parseTree(value: String): Tree = parseTree(value.asSequence())
        fun parseTree(line: Sequence<Char>): Tree = line.inOrder {
            when (val first = next()) {
                '[' -> Node(
                    parseTree(asSequence())
                        .also { nextExpect(',') },
                    parseTree(asSequence())
                ).also { nextExpect(']') }
                else -> Leaf(first.digitToInt())
            }
        }

    }
}
