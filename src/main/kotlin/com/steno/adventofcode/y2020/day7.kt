package com.steno.adventofcode.y2020

import com.steno.adventofcode.util.takeWhileNot
import com.steno.adventofcode.util.untilStable
import com.steno.assignment

data class BagType(val color: String) {
    override fun toString() = color
}

data class MinContents(val contents: Map<BagType, Int> = mapOf()) {
    val count
        get() = contents.values.sum()

    operator fun contains(type: BagType) = type in contents
    operator fun times(factor: Int) = MinContents(contents.mapValues { (_, count) -> count * factor })

    fun <R> map(fn: (Pair<BagType, Int>) -> R) = contents.map { (type, count) -> fn(type to count) }

    override fun toString() = contents.entries
        .map { (containedBag, count) -> "$count $containedBag" }
        .let { "{${it.joinToString()}}" }
}

data class Rules(val rules: Map<BagType, MinContents>) {
    operator fun get(type: BagType) = rules[type] ?: MinContents()

    fun typesDeepContaining(type: BagType) = generateSequence(typesContaining(type)) { types ->
        types + types.flatMap { type -> typesContaining(type) }
    }.untilStable { it.size }.last()

    private fun typesContaining(type: BagType) = rules.filter { (_, contents) -> type in contents }.keys

    fun minBagsIn(type: BagType) = generateSequence(listOf(this[type]), this::minBagsOfAllIn)
        .takeWhileNot { it.isEmpty() }
        .flatten()
        .sumOf { it.count }

    private fun minBagsOfAllIn(minBagContents: List<MinContents>) = minBagContents.flatMap {
        it.map { (containedType, count) -> this[containedType] * count }
    }
}

private fun main() {
    assignment("2020/day7") { parseRules(it) }
        .eval { rules -> rules.typesDeepContaining(BagType("shiny gold")).size }
        .eval { rules -> rules.minBagsIn(BagType("shiny gold")) }
}

private val RULE_FORMAT = Regex("^(.+) contain (.+)\\.$")
private val BAG_FORMAT = Regex("^(.+) bags?$")
private val CONTENT_FORMAT = Regex("^(\\d+) (.+)$")

private fun parseRules(lines: Sequence<String>) = Rules(lines.associate {
    RULE_FORMAT.parse(it) { (bagType, contents) ->
        parseBagType(bagType) to parseContents(contents)
    }
})

private fun parseBagType(text: String) = BAG_FORMAT.parse(text) { (color) -> BagType(color) }

private fun parseContents(text: String) = MinContents(when (text) {
    "no other bags" -> mapOf()
    else -> text.split(", ").associate {
        CONTENT_FORMAT.parse(it) { (count, bag) ->
            parseBagType(bag) to count.toInt()
        }
    }
})

private fun <R> Regex.parse(text: String, destructure: (MatchResult.Destructured) -> R) =
    matchEntire(text)!!.destructured.let(destructure)
