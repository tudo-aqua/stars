package tools.aqua.stars.core

/**
 * creates TxT cross product
 */
fun <T> List<T>.x2() =
    this.flatMap { a ->
        this.map { b ->
            a to b
        }
    }

/**
 * creates TxTxT cross product
 */
fun <T> List<T>.x3() =
    this.flatMap { a ->
        this.flatMap { b ->
            this.map { c ->
                Triple(a, b, c)
            }
        }
    }

/**
 * Adaption of com.marcinmoskala.math.powerset for lists while
 * preserving the order of the original list, going from small to big subsets
 * see https://github.com/MarcinMoskala/KotlinDiscreteMathToolkit/blob/master/src/main/java/com/marcinmoskala/math/PowersetExt.kt
 */
fun <T> List<T>.powerlist(): List<List<T>> = when {
    isEmpty() -> listOf(listOf())
    else -> dropLast(1).powerlist().let { it + it.map { it + last() } }.sortedBy { it.size }
}