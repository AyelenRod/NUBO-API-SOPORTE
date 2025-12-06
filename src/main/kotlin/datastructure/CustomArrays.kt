package com.soporte.datastructures

class DynamicProgressArray<T>(initialCapacity: Int = 10) {
    private var array: Array<Any?> = arrayOfNulls(initialCapacity)
    private var size = 0

    fun add(element: T) {
        if (size == array.size) {
            resize()
        }
        array[size++] = element
    }

    private fun resize() {
        val newArray = arrayOfNulls<Any>(array.size * 2)
        System.arraycopy(array, 0, newArray, 0, array.size)
        array = newArray
    }

    @Suppress("UNCHECKED_CAST")
    fun get(index: Int): T {
        if (index >= size) throw IndexOutOfBoundsException()
        return array[index] as T
    }

    fun size() = size

    fun toList(): List<T> {
        @Suppress("UNCHECKED_CAST")
        return array.take(size).map { it as T }
    }

    fun clear() {
        array = arrayOfNulls(10)
        size = 0
    }
}

class CircularArray<T>(private val capacity: Int) {
    private val array: Array<Any?> = arrayOfNulls(capacity)
    private var head = 0
    private var tail = 0
    private var size = 0

    fun add(element: T) {
        array[tail] = element
        tail = (tail + 1) % capacity

        if (size < capacity) {
            size++
        } else {
            head = (head + 1) % capacity
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun get(index: Int): T {
        if (index >= size) throw IndexOutOfBoundsException()
        val actualIndex = (head + index) % capacity
        return array[actualIndex] as T
    }

    fun size() = size

    fun toList(): List<T> {
        val result = mutableListOf<T>()
        for (i in 0 until size) {
            result.add(get(i))
        }
        return result
    }

    @Suppress("UNCHECKED_CAST")
    fun getLatest(): T? {
        if (size == 0) return null
        val index = if (tail == 0) capacity - 1 else tail - 1
        return array[index] as T?
    }
}