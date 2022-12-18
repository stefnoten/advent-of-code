package com.steno.adventofcode.y2021

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.parse

private class Day24 : AdventOfCodeSpec({ challenge ->
    challenge.map { parse(it) }
        .eval { program ->
            modelNumbers(14).first {
                program.apply(State(it))[Variable("z")] == 0L
            }
        }
}) {
    sealed interface Operand
    data class Variable(val name: String) : Operand
    data class Literal(val value: Long) : Operand

    data class State private constructor(val inputsDigits: Long, val values: Map<Variable, Long>) {
        constructor(input: Long) : this(input.toString().reversed().toLong(), emptyMap())

        operator fun get(operand: Operand) = when (operand) {
            is Variable -> values[operand] ?: 0
            is Literal -> operand.value
        }

        fun set(variable: Variable, value: Long) = copy(values = values + (variable to value))
        fun read() = (inputsDigits % 10) to copy(inputsDigits = inputsDigits / 10)
    }

    sealed class Operation {
        abstract fun apply(state: State): State
    }

    data class InputOp(val a: Variable) : Operation() {
        override fun apply(state: State) = state.read().let { (value, newState) -> newState.set(a, value) }
    }

    data class AddOp(val a: Variable, val b: Operand) : Operation() {
        override fun apply(state: State) = state.set(a, state[a] + state[b])
    }

    data class MulOp(val a: Variable, val b: Operand) : Operation() {
        override fun apply(state: State) = state.set(a, state[a] * state[b])
    }

    data class DivOp(val a: Variable, val b: Operand) : Operation() {
        override fun apply(state: State) = state.set(a, state[a] / state[b])
    }

    data class ModOp(val a: Variable, val b: Operand) : Operation() {
        override fun apply(state: State) = state.set(a, state[a] % state[b])
    }

    data class EqualOp(val a: Variable, val b: Operand) : Operation() {
        override fun apply(state: State) = state.set(a, if (state[a] == state[b]) 1 else 0)
    }

    data class Program(val ops: List<Operation>) : Operation() {
        override fun apply(state: State) = ops.fold(state) { acc, operation -> operation.apply(acc) }
    }

    companion object {

        fun modelNumbers(digitCount: Int): Sequence<Long> = when (digitCount) {
            1 -> (9L downTo 1L).asSequence()
            else -> modelNumbers(1).flatMap { digit1 -> modelNumbers(digitCount - 1).map { digit1 * 10 + it } }
        }

        val OP_FORMAT = Regex("([a-z]+) ([a-z])( ([a-z]|-?\\d+))?")

        fun parse(lines: Sequence<String>) = lines.map { line ->
            OP_FORMAT.parse(line) { (operator, operand1, _, operand2) ->
                when (operator) {
                    "inp" -> InputOp(Variable(operand1))
                    "add" -> AddOp(Variable(operand1), parseOperand(operand2))
                    "mul" -> MulOp(Variable(operand1), parseOperand(operand2))
                    "div" -> DivOp(Variable(operand1), parseOperand(operand2))
                    "mod" -> ModOp(Variable(operand1), parseOperand(operand2))
                    "eql" -> EqualOp(Variable(operand1), parseOperand(operand2))
                    else -> throw IllegalStateException("Unknown operator $operator")
                }
            }
        }.let { Program(it.toList()) }

        fun parseOperand(value: String) = value.toLongOrNull()?.let { Literal(it) } ?: Variable(value)
    }
}
