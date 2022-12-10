package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.spec.mapEach
import com.steno.adventofcode.util.takeUntil
import com.steno.adventofcode.y2022.Day10.AddX
import com.steno.adventofcode.y2022.Day10.Noop

class Day10 : AdventOfCodeSpec({ challenge ->
    challenge.mapEach { it.toInstruction() }
        .map { it + generateSequence { Noop } }
        .map { it.iterator() }
        .map { instructions ->
            generateSequence(State()) {
                it.tick { instructions.next() }
            }.drop(1)
        }
        .eval(-720, 13140, 12640) { states ->
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
        val cycle: Int = 0,
        val x: Int = 1,
        val remainingCycles: Int = 0,
        val action: () -> Int = { x },
    ) {
        val signalStrength = cycle * x
        private val spritePixels = x - 1..x + 1
        private val drawingPixel = cycle % 40 - 1
        val lit = drawingPixel in spritePixels

        fun tick(next: () -> Instruction) = when (remainingCycles) {
            0 -> copy(x = action()).handle(next())
            else -> copy(cycle = cycle + 1, remainingCycles = remainingCycles - 1)
        }

        private fun handle(instruction: Instruction) = when (instruction) {
            Noop -> copy(cycle = cycle + 1, remainingCycles = 0, action = { x })
            is AddX -> copy(cycle = cycle + 1, remainingCycles = 1, action = { x + instruction.value })
        }

        override fun toString() = "[$cycle] X=$x (remaining: $remainingCycles)"
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
