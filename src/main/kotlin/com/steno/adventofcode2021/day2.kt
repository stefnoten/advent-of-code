package com.steno.adventofcode2021

enum class Direction {
    forward,
    down,
    up
}

data class Move(val direction: Direction, val steps: Int) {
    override fun toString() = "$direction $steps"
}

data class Location(val horizontal: Int = 0, val depth: Int = 0, val aim: Int = 0) {
    fun move(move: Move) = when (move.direction) {
        Direction.forward -> copy(horizontal = horizontal + move.steps)
        Direction.down -> copy(depth = depth + move.steps)
        Direction.up -> copy(depth = depth - move.steps)
    }

    fun moveAimed(move: Move) = when (move.direction) {
        Direction.forward -> copy(horizontal = horizontal + move.steps, depth = depth + aim * move.steps)
        Direction.down -> copy(aim = aim + move.steps)
        Direction.up -> copy(aim = aim - move.steps)
    }

    val value: Int
        get() = horizontal * depth

    override fun toString() = "($horizontal, ${-depth}) ∠ ${-aim}"
}

private fun main() {
    Assignment("day2", ::toMove)
        .eval { it.fold(Location(), Location::move).value }
        .eval { it.fold(Location(), Location::moveAimed).value }
}

fun toMove(value: String) = value.split(' ').let { (direction, steps) -> Move(Direction.valueOf(direction), steps.toInt()) }
