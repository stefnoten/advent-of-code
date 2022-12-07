package com.steno.adventofcode.y2022

import com.steno.adventofcode.spec.AdventOfCodeSpec
import java.lang.IllegalStateException
import java.nio.file.Path

val ROOT: Path = Path.of("/")

class Day7 : AdventOfCodeSpec({ challenge ->
    challenge.map { lines -> lines.fold(State(), State::process) }
        .eval { state -> state.sizePerDir.filterValues { it <= 100000 }.values.sum() }
        .eval { state ->
            state.sizePerDir.let { sizes ->
                val unused = 70000000 - sizes[ROOT]!!
                val toBeFreed = 30000000 - unused
                sizes.asSequence()
                    .filter { it.value >= toBeFreed }
                    .sortedBy { it.value }
                    .first()
                    .value
            }
        }
}) {

    data class State(
        val workingDirectory: Path = ROOT,
        val listing: Boolean = false,
        val files: Map<Path, Int> = mapOf(),
    ) {
        val sizePerDir
            get() = files.asSequence()
                .flatMap { (path, size) ->
                    generateSequence(path.parent) { it.parent }
                        .map { it to size }
                }
                .groupingBy { it.first }
                .aggregate { _, total: Int?, (_, size), _ -> (total ?: 0) + size }

        fun process(line: String) = when {
            line.startsWith("$ ") -> copy(listing = false).command(line.drop(2))
            listing -> capture(line)
            else -> throw IllegalStateException()
        }

        fun command(command: String) = when {
            command.startsWith("cd ") -> cd(command.drop(3))
            command == "ls" -> ls()
            else -> throw IllegalStateException()
        }

        fun cd(path: String) = cd(workingDirectory.resolve(path).normalize())
        fun cd(path: Path) = copy(workingDirectory = path)
        fun ls() = copy(listing = true)
        fun capture(line: String) = when {
            line.startsWith("dir ") -> captureDir(line.drop(4))
            else -> line.split(' ').let { (size, name) -> captureFile(name, size.toInt()) }
        }

        fun captureDir(name: String) = this
        fun captureFile(name: String, size: Int) = captureFile(workingDirectory.resolve(name), size)
        fun captureFile(path: Path, size: Int) = copy(files = files + (path to size))
    }
}
