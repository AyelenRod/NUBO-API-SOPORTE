package com.soporte.datastructures

/**
 * CONJUNTO: HashSet personalizado para mantener IDs únicos de estudiantes
 * que han completado ciertas actividades
 */
class CustomHashSet<T> {
    private val buckets = Array<MutableList<T>?>(16) { null }
    private var size = 0
    private val loadFactor = 0.75

    private fun hash(element: T): Int {
        return Math.abs(element.hashCode() % buckets.size)
    }

    fun add(element: T): Boolean {
        if (size >= buckets.size * loadFactor) {
            resize()
        }

        val index = hash(element)
        if (buckets[index] == null) {
            buckets[index] = mutableListOf()
        }

        val bucket = buckets[index]!!
        if (bucket.contains(element)) {
            return false
        }

        bucket.add(element)
        size++
        return true
    }

    fun contains(element: T): Boolean {
        val index = hash(element)
        val bucket = buckets[index] ?: return false
        return bucket.contains(element)
    }

    fun remove(element: T): Boolean {
        val index = hash(element)
        val bucket = buckets[index] ?: return false

        if (bucket.remove(element)) {
            size--
            return true
        }
        return false
    }

    fun size() = size

    fun isEmpty() = size == 0

    fun toList(): List<T> {
        val result = mutableListOf<T>()
        buckets.forEach { bucket ->
            bucket?.let { result.addAll(it) }
        }
        return result
    }

    fun clear() {
        buckets.fill(null)
        size = 0
    }

    private fun resize() {
        val oldBuckets = buckets.clone()
        buckets.fill(null)
        size = 0

        oldBuckets.forEach { bucket ->
            bucket?.forEach { element ->
                add(element)
            }
        }
    }

    fun union(other: CustomHashSet<T>): CustomHashSet<T> {
        val result = CustomHashSet<T>()
        this.toList().forEach { result.add(it) }
        other.toList().forEach { result.add(it) }
        return result
    }

    fun intersection(other: CustomHashSet<T>): CustomHashSet<T> {
        val result = CustomHashSet<T>()
        this.toList().forEach { element ->
            if (other.contains(element)) {
                result.add(element)
            }
        }
        return result
    }
}