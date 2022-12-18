package com.steno.adventofcode.y2021

import com.steno.adventofcode.spec.AdventOfCodeSpec
import com.steno.adventofcode.util.inOrder
import kotlin.math.abs

private class Day23 : AdventOfCodeSpec({ challenge ->
    challenge.map { parse(it) }
        .eval(12521, 16059, 44169, 43117) { it.costToOrganise() }
}) {
    enum class Type(val energyPerStep: Int) {
        A(1),
        B(10),
        C(100),
        D(1000);

        companion object {
            fun find(value: Char) = values().find { it.name == "$value" }
        }
    }

    data class Amphipod(val type: Type, val lostEnergy: Int = 0) {
        fun move(distance: Int) = copy(lostEnergy = lostEnergy + distance * type.energyPerStep)
    }

    data class Hallway(val range: IntRange, val pods: Map<Int, Amphipod> = emptyMap()) {
        val occupiedSpots get() = pods.keys
        fun podsAround(targetX: Int) = sequence {
            podLeftOf(targetX)?.let { yield(it) }
            podRightOf(targetX)?.let { yield(it) }
        }

        private fun podLeftOf(targetX: Int) = pods.filter { (x, _) -> x < targetX }.maxByOrNull { (x, _) -> x }
        private fun podRightOf(targetX: Int) = pods.filter { (x, _) -> x > targetX }.minByOrNull { (x, _) -> x }

        fun isEmpty() = occupiedSpots.isEmpty()

        fun freeSpotsFrom(startX: Int): IntRange {
            val start = occupiedSpots.filter { it < startX }.maxOrNull()?.let { it + 1 } ?: range.first
            val end = occupiedSpots.filter { it > startX }.minOrNull()?.let { it - 1 } ?: range.last
            return start..end
        }

        fun addAt(x: Int, pod: Amphipod) = copy(pods = pods + (x to pod))
        fun removeAt(x: Int) = copy(pods = pods - x)

        override fun toString() = "#${range.joinToString("") { pods[it]?.type?.name ?: "." }}#"
    }

    data class Room(val x: Int, val type: Type, val size: Int, val pods: List<Amphipod>) {
        val presentPodsOrganised = pods.all { it.type == type }
        val allPodsOrganised = pods.size == size && presentPodsOrganised
        val freeSpots = size - pods.size

        fun tryExitOne() = when {
            presentPodsOrganised || pods.isEmpty() -> null
            else -> Pair(
                pods.first().move(freeSpots + 1),
                copy(pods = pods.drop(1))
            )
        }

        fun tryEnterFrom(pod: Amphipod, fromX: Int) = when {
            freeSpots == 0 || !presentPodsOrganised || pod.type != type -> null
            else -> copy(pods = listOf(pod.move(freeSpots + abs(fromX - x))) + pods)
        }
    }

    data class Diagram(val hallway: Hallway, val rooms: Map<Int, Room>) {
        val range = 0..(hallway.range.last + 1)
        val organised = rooms.values.all { it.allPodsOrganised }
        val cost get() = rooms.values.sumOf { room -> room.pods.sumOf { it.lostEnergy } }

        fun organise(): Sequence<Diagram> = when (organised) {
            true -> sequenceOf(this)
            false -> allMoves().flatMap { it.organise() }
        }

        fun costToOrganise() = organise().minOf { it.cost }

        fun allMoves() = exitRoomMoves() + hallwayToRoomMoves()

        fun exitRoomMoves() = rooms.values.asSequence()
            .flatMap { room ->
                room.tryExitOne()
                    ?.let { (exitedPod, exitedRoom) -> exitRoomMovesFor(exitedPod, exitedRoom) }
                    ?: emptySequence()
            }

        fun exitRoomMovesFor(exitedPod: Amphipod, exitedRoom: Room) = hallway.freeSpotsFrom(exitedRoom.x).let { freeSpots ->
            val enteredRoom = rooms.values.asSequence()
                .filter { to -> to.x in freeSpots }
                .mapNotNull { to -> to.tryEnterFrom(exitedPod, exitedRoom.x) }
                .firstOrNull()
            when (enteredRoom) {
                null -> freeSpots.asSequence()
                    .filter { it !in rooms }
                    .map { x ->
                        copy(
                            rooms = rooms + (exitedRoom.x to exitedRoom),
                            hallway = hallway.addAt(x, exitedPod.move(abs(exitedRoom.x - x))),
                        )
                    }

                else -> sequenceOf(
                    copy(
                        rooms = rooms
                                + (exitedRoom.x to exitedRoom)
                                + (enteredRoom.x to enteredRoom)
                    )
                )
            }
        }

        fun hallwayToRoomMoves() = rooms.values.asSequence()
            .flatMap { room ->
                hallway.podsAround(room.x)
                    .mapNotNull { (x, pod) ->
                        room.tryEnterFrom(pod, x)?.let { enteredRoom ->
                            copy(
                                hallway = hallway.removeAt(x),
                                rooms = rooms + (enteredRoom.x to enteredRoom)
                            )
                        }
                    }
            }

        override fun toString(): String {
            val fullWall = range.joinToString("") { "#" }
            val roomRange = (rooms.keys.minOrNull()!! - 1)..(rooms.keys.maxOrNull()!! + 1)
            val roomWalls = range.joinToString("") { if (it in roomRange) "#" else " " }
            return "\n" +
                    "$fullWall\n" +
                    "$hallway\n" +
                    (0 until rooms.maxOf { (_, room) -> room.size }).joinToString("\n") { y ->
                        val background = if (y == 0) fullWall else roomWalls
                        background
                            .mapIndexed { x, bg ->
                                rooms[x]
                                    ?.let { room ->
                                        (y - room.freeSpots).let {
                                            if (it >= 0) room.pods[it].type.name else "."
                                        }
                                    }
                                    ?: bg
                            }
                            .joinToString("")
                    } + "\n" +
                    "$roomWalls\n"
        }
    }

    companion object {
        fun parse(lines: Sequence<String>) = lines.inOrder {
            next().all { it == '#' } || throw IllegalStateException()
            val hallway = next().run { Hallway(indexOf('.')..lastIndexOf('.')) }
            val podRows = next {
                takeWhile { char -> char.any { it !in "# " } }.map { row ->
                    row.mapIndexedNotNull { x, type ->
                        Type.find(type)?.let { x to Amphipod(it) }
                    }.toMap()
                }.toList()
            }
            val roomEntrances = podRows.flatMap { it.keys }.distinct().sorted()
            Diagram(
                hallway = hallway,
                rooms = roomEntrances.associateWith { x ->
                    Room(
                        x = x,
                        type = Type.values()[roomEntrances.indexOf(x)],
                        size = podRows.size,
                        pods = podRows.mapNotNull { row -> row[x] }
                    )
                }
            )
        }
    }
}
