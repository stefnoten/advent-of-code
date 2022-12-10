package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.spec.mapEach
import com.steno.adventofcode.util.takeUntil
import com.steno.adventofcode.y2022.Day10.AddX
import com.steno.adventofcode.y2022.Day10.Noop

class Day10 : AdventOfCodeSpec({ challenge ->
    challenge
        .mapEach { it.toInstruction() }
        .map { it + generateSequence { Noop } }
        .map { it.iterator() }
        .map { instructions ->
            generateSequence(State(instruction = instructions.next())) {
                it.tick(instructions::next)
            }
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
        val cycle: Int = 1,
        val x: Int = 1,
        val instruction: Instruction = Noop,
        val instructionCompletedAt: Int = cycle + instruction.cycles
    ) {
        val instructionCompleted = cycle >= instructionCompletedAt

        val signalStrength get() = cycle * x

        val spritePixels get() = x - 1..x + 1
        val drawingPixel get() = (cycle - 1) % 40
        val lit get() = drawingPixel in spritePixels

        fun tick(next: () -> Instruction) = copy(cycle = cycle + 1).maybeComplete(next)

        private fun maybeComplete(next: () -> Instruction) = if (instructionCompleted) complete(next()) else this
        private fun complete(newInstruction: Instruction) = State(
            cycle = cycle,
            x = instruction.apply(x),
            instruction = newInstruction
        )

        override fun toString() = "@$cycle \tX=$x"
    }

    sealed interface Instruction {
        val cycles: Int
        fun apply(x: Int): Int
    }

    object Noop : Instruction {
        override val cycles = 1
        override fun apply(x: Int) = x
        override fun toString() = "noop"
    }

    data class AddX(val value: Int) : Instruction {
        override val cycles = 2
        override fun apply(x: Int) = x + value
        override fun toString() = "addx $value"
    }
}

private fun String.toInstruction() = when (this) {
    "noop" -> Noop
    else -> AddX(drop("addx ".length).toInt())
}
