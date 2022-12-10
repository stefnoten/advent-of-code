package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.spec.mapEach
import com.steno.adventofcode.util.takeUntil
import com.steno.adventofcode.y2022.Day10.AddX
import com.steno.adventofcode.y2022.Day10.Noop

class Day10 : AdventOfCodeSpec({ challenge ->
    challenge
        .mapEach { it.toInstruction() }
        .map { instructions ->
            instructions
                .flatMap {
                    when (it) {
                        is AddX -> sequenceOf(Noop, it)
                        else -> sequenceOf(it)
                    }
                }
                .runningFold(State()) { state, instruction ->
                    when (instruction) {
                        is AddX -> state.copy(cycle = state.cycle + 1, x = state.x + instruction.value)
                        is Noop -> state.copy(cycle = state.cycle + 1)
                    }
                }
        }
        .eval(0, 13140, 12640) { states ->
            states
                .filter { (it.cycle - 20) % 40 == 0 }
                .takeUntil { it.cycle >= 220 }
                .sumOf { it.signalStrength }
        }
        .eval { states ->
            states.chunked(40).take(6)
                .joinToString("\n") { line ->
                    line.joinToString("") {
                        if (it.lit) "#" else "."
                    }
                }
        }
}) {
    data class State(
        val cycle: Int = 1,
        val x: Int = 1,
    ) {
        val signalStrength = cycle * x
        private val spritePixels = x - 1..x + 1
        private val drawingPixel = cycle % 40 - 1
        val lit = drawingPixel in spritePixels

        override fun toString() = "[$cycle] X=$x"
    }

    sealed interface Instruction {
        fun apply(x: Int): Int
    }

    object Noop : Instruction {
        override fun apply(x: Int) = x
        override fun toString() = "noop"
    }

    data class AddX(val value: Int) : Instruction {
        override fun apply(x: Int) = x + value
        override fun toString() = "addx $value"
    }
}

private fun String.toInstruction() = when (this) {
    "noop" -> Noop
    else -> AddX(drop("addx ".length).toInt())
}
