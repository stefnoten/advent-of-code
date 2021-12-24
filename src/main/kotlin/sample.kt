import kotlin.math.max

class MyPair(val first: Int, val last: Int)

fun main() {
    generateSequence(1) { it + 1 }
        .forEach { n ->
            val pair = MyPair(max(1, 1), max(2, 2))
            if (pair.first != 1) {
                throw IllegalStateException("Unexpected mismatch: ${pair.first} != 1 (after $n iterations)")
            }
        }
}
