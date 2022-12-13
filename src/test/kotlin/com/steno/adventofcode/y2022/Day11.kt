package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.*

class Day11 : AdventOfCodeSpec({ challenge ->
    challenge.map { State.parse(it) }
        .eval(10605, 95472) { initial ->
            initial.copy(reduceWorry = { it / 3 })
                .rounds(20)
                .last().monkeyBusinessLevel
        }
        .eval(2713310158, 17926061332) { initial ->
            val totalDivisor = initial.specs.map { it.divisor }.reduce(Long::times)
            initial.copy(reduceWorry = { it % totalDivisor })
                .rounds(10000)
                .onChange({ it.round }) { state ->
                    if (state.round == 2 || state.round == 21 || (state.round - 1) % 1000 == 0) {
                        state.printInspections()
                    }
                }
                .last().monkeyBusinessLevel
        }
}) {
    data class State(
        val specs: List<MonkeySpec>,
        val items: Map<Monkey, List<Long>>,
        val inspections: Map<Monkey, Long> = mapOf(),
        val round: Int = 0,
        val reduceWorry: (Long) -> Long = { it },
    ) {
        private val monkeys get() = specs.indices.asSequence().map { Monkey(it) }
        val monkeyBusinessLevel
            get() = inspections.values.sortedDescending().take(2).reduce(Long::times)

        fun rounds(rounds: Int): Sequence<State> = (1..rounds).asSequence()
            .flatScan(this) { state, _ -> state.round() }

        fun round(): Sequence<State> = monkeys
            .flatScan(copy(round = round + 1)) { state, monkey -> state.turn(monkey) }
            .drop(1)

        fun turn(monkey: Monkey): Sequence<State> = generateSequence(inspect(monkey)) { it.inspect(monkey) }

        fun inspect(currentMonkey: Monkey): State? {
            val mySpec = specs[currentMonkey.n]
            val myItems = items[currentMonkey]!!
            val item = myItems.firstOrNull() ?: return null

            val nextItem = reduceWorry(mySpec.operation(item))
            val targetMonkey = mySpec.nextMonkey(nextItem)
            val targetNewItems = items[targetMonkey]!! + nextItem

            return copy(
                items = items
                        + (currentMonkey to myItems.drop(1))
                        + (targetMonkey to targetNewItems),
                inspections = inspections + (currentMonkey to ((inspections[currentMonkey] ?: 0) + 1))
            )
        }

        fun printInspections() {
            println("== After round ${round - 1} ==")
            println(inspections.entries.joinToString("\n") {
                "${it.key} inspected items ${it.value} times."
            })
            println()
        }

        override fun toString(): String = items.entries
            .sortedBy { (monkey, _) -> monkey.n }
            .joinToString("\n") { (monkey, items) -> "$monkey: $items" } + "\n"

        companion object {
            fun parse(lines: Sequence<String>) = lines.split { it.isEmpty() }
                .map { monkeyLines ->
                    monkeyLines.inOrder {
                        val n = next().removeSurrounding("Monkey ", ":").toInt().let { Monkey(it) }
                        val items = next().removePrefix("  Starting items: ").split(", ").map { it.toLong() }
                        val operation = next().removePrefix("  Operation: new = old ").split(' ').let { (operatorStr, operandStr) ->
                            val operand: (Long) -> Long = if (operandStr == "old") { x -> x } else { _ -> operandStr.toLong() }
                            when (operatorStr) {
                                "+" -> { x: Long -> x + operand(x) }
                                "*" -> { x: Long -> x * operand(x) }
                                else -> throw IllegalStateException("Unknown operator: $operatorStr")
                            }
                        }
                        val divisor = next().removePrefix("  Test: divisible by ").toLong()
                        val nextIfTrue = next().removePrefix("    If true: throw to monkey ").toInt().let { Monkey(it) }
                        val nextIfFalse = next().removePrefix("    If false: throw to monkey ").toInt().let { Monkey(it) }
                        n to Pair(
                            MonkeySpec(divisor, operation) { if (it % divisor == 0L) nextIfTrue else nextIfFalse },
                            items
                        )
                    }
                }
                .toList()
                .let { monkeys ->
                    State(
                        monkeys.map { (_, pair) -> pair.first },
                        monkeys.associate { (n, pair) -> n to pair.second },
                    )
                }
        }
    }

    data class MonkeySpec(
        val divisor: Long,
        val operation: (Long) -> Long,
        val nextMonkey: (Long) -> Monkey
    )

    @JvmInline
    value class Monkey(val n: Int) {
        override fun toString(): String = "Monkey $n"
    }
}
