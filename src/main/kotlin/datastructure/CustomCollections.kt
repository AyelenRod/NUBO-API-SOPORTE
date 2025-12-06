package com.soporte.datastructures

import java.util.*

/**
 * LISTA: Lista enlazada personalizada para mantener orden de recomendaciones
 */
class CustomLinkedList<T> {
    private data class Node<T>(
        var data: T,
        var next: Node<T>? = null,
        var prev: Node<T>? = null
    )

    private var head: Node<T>? = null
    private var tail: Node<T>? = null
    private var size = 0

    fun addFirst(element: T) {
        val newNode = Node(element)
        if (head == null) {
            head = newNode
            tail = newNode
        } else {
            newNode.next = head
            head?.prev = newNode
            head = newNode
        }
        size++
    }

    fun addLast(element: T) {
        val newNode = Node(element)
        if (tail == null) {
            head = newNode
            tail = newNode
        } else {
            tail?.next = newNode
            newNode.prev = tail
            tail = newNode
        }
        size++
    }

    fun removeFirst(): T? {
        if (head == null) return null
        val data = head!!.data
        head = head!!.next
        head?.prev = null
        size--
        if (size == 0) tail = null
        return data
    }

    fun getFirst(): T? = head?.data

    fun getLast(): T? = tail?.data

    fun size() = size

    fun toList(): List<T> {
        val result = mutableListOf<T>()
        var current = head
        while (current != null) {
            result.add(current.data)
            current = current.next
        }
        return result
    }

    fun clear() {
        head = null
        tail = null
        size = 0
    }
}

/**
 * PILA: Stack para mantener historial de navegación de actividades
 */
class CustomStack<T> {
    private val items = mutableListOf<T>()

    fun push(element: T) {
        items.add(element)
    }

    fun pop(): T? {
        if (isEmpty()) return null
        return items.removeAt(items.size - 1)
    }

    fun peek(): T? {
        if (isEmpty()) return null
        return items[items.size - 1]
    }

    fun isEmpty() = items.isEmpty()

    fun size() = items.size

    fun toList(): List<T> = items.toList()

    fun clear() {
        items.clear()
    }
}

/**
 * COLA: Queue para procesar recomendaciones en orden
 */
class CustomQueue<T> {
    private val items = LinkedList<T>()

    fun enqueue(element: T) {
        items.addLast(element)
    }

    fun dequeue(): T? {
        if (isEmpty()) return null
        return items.removeFirst()
    }

    fun peek(): T? {
        if (isEmpty()) return null
        return items.first
    }

    fun isEmpty() = items.isEmpty()

    fun size() = items.size

    fun toList(): List<T> = items.toList()

    fun clear() {
        items.clear()
    }
}