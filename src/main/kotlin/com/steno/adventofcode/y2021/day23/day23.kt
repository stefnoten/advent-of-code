package com.steno.adventofcode.y2021.day23

import com.steno.adventofcode.util.inOrder
import com.steno.assignment
import kotlin.math.abs

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

data class Room(val x: Int, val type: Type, val top: Amphipod?, val bottom: Amphipod?) {
    val freeSpot = top == null && (bottom == null || bottom.type == type)
    val organised = top != null && bottom != null && top.type == type && bottom.type == type

    fun tryExitOne() = when {
        top != null -> top.takeUnless { it.type == type && bottom!!.type == type }
            ?.let {
                Pair(it.move(1), copy(top = null))
            }
        bottom != null -> bottom.takeUnless { it.type == type }
            ?.let {
                Pair(it.move(2), copy(bottom = null))
            }
        else -> null
    }

    fun tryEnterFrom(pod: Amphipod, fromX: Int) = when {
        pod.type != type || !freeSpot -> null
        bottom == null -> copy(bottom = pod.move(2 + abs(fromX - x)))
        else -> copy(top = pod.move(1 + abs(fromX - x)))
    }
}

data class Diagram(val hallway: Hallway, val rooms: Map<Int, Room>) {
    val range = 0..(hallway.range.last + 1)
    val organised = rooms.values.all { it.organised }
    val cost get() = rooms.values.sumOf { (it.top?.lostEnergy ?: 0) + (it.bottom?.lostEnergy ?: 0) }

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

    override fun toString() = "\n" +
            range.joinToString("") { "#" } + "\n" +
            hallway + "\n" +
            range.joinToString("") { x ->
                rooms[x]
                    ?.let { it.top?.type?.name ?: "." }
                    ?: "#"
            } + "\n" +
            range.joinToString("") { x ->
                rooms[x]
                    ?.let { it.bottom?.type?.name ?: "." }
                    ?: "#".takeIf { x - 1 in rooms || x + 1 in rooms }
                    ?: " "
            } + "\n" +
            range.joinToString("") { x ->
                " ".takeIf { rooms.keys.all { it > x + 1 } || rooms.keys.all { it < x - 1 } }
                    ?: "#"
            } + "\n"
}

private fun main() {
    assignment("2021/day23") { parse(it) }
        .eval { it.costToOrganise() }
}

fun parse(lines: Sequence<String>) = lines.inOrder {
    next().all { it == '#' } || throw IllegalStateException()
    val hallway = next().run { Hallway(indexOf('.')..lastIndexOf('.')) }
    val podsTop = next().mapIndexedNotNull { x, type -> Type.find(type)?.let { x to Amphipod(it) } }.toMap()
    val podsBottom = next().mapIndexedNotNull { x, type -> Type.find(type)?.let { x to Amphipod(it) } }.toMap()
    val roomEntrances = (podsTop.keys + podsBottom.keys).sorted()
    Diagram(
        hallway = hallway,
        rooms = roomEntrances.associateWith { Room(it, Type.values()[roomEntrances.indexOf(it)], podsTop[it], podsBottom[it]) }
    )
}
