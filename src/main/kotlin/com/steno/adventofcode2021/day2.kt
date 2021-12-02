package com.steno.adventofcode2021

import com.steno.adventofcode2021.Direction.*
import java.util.*

fun main() {
    Assignment("day2", Move::parse)
        .eval { it.fold(Location(), ::move).value }
        .eval { it.fold(Location(), ::moveAimed).value }
}

data class Move(val direction: Direction, val steps: Int) {
    override fun toString() = "$direction $steps"

    companion object {
        fun parse(value: String) = value.split(' ').let { (direction, steps) -> Move(Direction.parse(direction), steps.toInt()) }
    }
}

enum class Direction {
    FORWARD, DOWN, UP;

    companion object {
        fun parse(value: String) = valueOf(value.uppercase(Locale.getDefault()))
    }
}

data class Location(val horizontal: Int = 0, val depth: Int = 0, val aim: Int = 0) {
    val value get() = horizontal * depth

    override fun toString() = "($horizontal, ${-depth}) ∠ ${-aim}"
}

fun move(location: Location, move: Move) = when (move.direction) {
    FORWARD -> location.copy(horizontal = location.horizontal + move.steps)
    DOWN -> location.copy(depth = location.depth + move.steps)
    UP -> location.copy(depth = location.depth - move.steps)
}

fun moveAimed(location: Location, move: Move) = when (move.direction) {
    FORWARD -> location.copy(horizontal = location.horizontal + move.steps, depth = location.depth + location.aim * move.steps)
    DOWN -> location.copy(aim = location.aim + move.steps)
    UP -> location.copy(aim = location.aim - move.steps)
}
