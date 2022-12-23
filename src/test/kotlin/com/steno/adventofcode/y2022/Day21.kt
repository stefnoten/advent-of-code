package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.spec.mapEach

class Day21 : AdventOfCodeSpec({ challenge ->
    challenge.mapEach { it.toMonkey() }.map { Monkeys(it.toMap()) }
        .eval(152, 291425799367130) { it.calculate("root") }
        .eval(301, 3219579395609) { monkeys ->
            monkeys
                .override("root") {
                    val op = it as Op
                    MinusOp(op.dep1, op.dep2)
                }
                .override("humn") { Unknown }
                .solveUnknownFor("root", 0)
        }

}) {
    data class Monkeys(val monkeys: Map<String, Monkey>) {
        fun calculate(monkey: String): Long? = monkeys[monkey]!!.calculate { calculate(it) }
        fun solveUnknownFor(startMonkey: String, value: Long) = monkeys[startMonkey]!!.solveUnknown(value, this)

        fun override(monkey: String, map: (Monkey) -> Monkey) = Monkeys(
            monkeys + (monkey to map(monkeys[monkey]!!))
        )
    }

    interface Monkey {
        fun calculate(lookup: (String) -> Long?): Long?
        fun solveUnknown(value: Long, monkeys: Monkeys): Long?
    }

    class Fixed(val number: Long) : Monkey {
        override fun calculate(lookup: (String) -> Long?) = number
        override fun solveUnknown(value: Long, monkeys: Monkeys) = null
    }

    abstract class Op(val dep1: String, val dep2: String, val fn: (Long, Long) -> Long) : Monkey {
        override fun calculate(lookup: (String) -> Long?): Long? {
            val val1 = lookup(dep1)
            val val2 = lookup(dep2)
            return if (val1 == null || val2 == null) null else fn(val1, val2)
        }

        override fun solveUnknown(value: Long, monkeys: Monkeys): Long? {
            val val1 = monkeys.calculate(dep1)
            val val2 = monkeys.calculate(dep2)
            return when {
                val1 == null && val2 == null -> null
                val1 != null -> monkeys.solveUnknownFor(dep2, solveDep2(val1, value))
                val2 != null -> monkeys.solveUnknownFor(dep1, solveDep1(val2, value))
                else -> null
            }
        }

        abstract fun solveDep1(value2: Long, result: Long): Long
        abstract fun solveDep2(value1: Long, result: Long): Long
    }

    class PlusOp(dep1: String, dep2: String) : Op(dep1, dep2, Long::plus) {
        override fun solveDep1(value2: Long, result: Long) = result - value2
        override fun solveDep2(value1: Long, result: Long) = result - value1
    }

    class MinusOp(dep1: String, dep2: String) : Op(dep1, dep2, Long::minus) {
        override fun solveDep1(value2: Long, result: Long) = result + value2
        override fun solveDep2(value1: Long, result: Long) = value1 - result
    }

    class TimesOp(dep1: String, dep2: String) : Op(dep1, dep2, Long::times) {
        override fun solveDep1(value2: Long, result: Long) = result / value2
        override fun solveDep2(value1: Long, result: Long) = result / value1
    }
    class DivOp(dep1: String, dep2: String) : Op(dep1, dep2, Long::div) {
        override fun solveDep1(value2: Long, result: Long) = result * value2
        override fun solveDep2(value1: Long, result: Long) = value1 / result
    }

    object Unknown : Monkey {
        override fun calculate(lookup: (String) -> Long?): Long? = null
        override fun solveUnknown(value: Long, monkeys: Monkeys) = value
    }

    companion object {
        fun String.toMonkey() = split(": ").let { (name, op) ->
            name to (op.toFixed() ?: op.toOp())!!
        }

        fun String.toFixed() = toLongOrNull()?.let { Fixed(it) }
        fun String.toOp() = split(" ").let { (dep1, op, dep2) ->
            when (op) {
                "+" -> PlusOp(dep1, dep2)
                "-" -> MinusOp(dep1, dep2)
                "*" -> TimesOp(dep1, dep2)
                "/" -> DivOp(dep1, dep2)
                else -> null
            }
        }
    }
}
