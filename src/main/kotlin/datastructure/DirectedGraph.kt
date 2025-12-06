package com.soporte.datastructures

/**
 * GRAFO DIRIGIDO: Para representar dependencias y relaciones entre actividades
 * Útil para determinar rutas de aprendizaje y prerrequisitos
 */
class DirectedGraph<T> {
    private val adjacencyList = mutableMapOf<T, MutableList<Edge<T>>>()

    data class Edge<T>(
        val destination: T,
        val weight: Double,
        val label: String = ""
    )

    fun addVertex(vertex: T) {
        if (!adjacencyList.containsKey(vertex)) {
            adjacencyList[vertex] = mutableListOf()
        }
    }

    fun addEdge(from: T, to: T, weight: Double = 1.0, label: String = "") {
        addVertex(from)
        addVertex(to)
        adjacencyList[from]?.add(Edge(to, weight, label))
    }

    fun getNeighbors(vertex: T): List<Edge<T>> {
        return adjacencyList[vertex] ?: emptyList()
    }

    fun hasPath(from: T, to: T): Boolean {
        if (from == to) return true

        val visited = mutableSetOf<T>()
        val stack = mutableListOf(from)

        while (stack.isNotEmpty()) {
            val current = stack.removeAt(stack.size - 1)
            if (current == to) return true

            if (current in visited) continue
            visited.add(current)

            adjacencyList[current]?.forEach { edge ->
                if (edge.destination !in visited) {
                    stack.add(edge.destination)
                }
            }
        }

        return false
    }

    fun findShortestPath(from: T, to: T): List<T>? {
        if (from == to) return listOf(from)

        val distances = mutableMapOf<T, Double>()
        val previous = mutableMapOf<T, T?>()
        val unvisited = adjacencyList.keys.toMutableSet()

        adjacencyList.keys.forEach { distances[it] = Double.POSITIVE_INFINITY }
        distances[from] = 0.0

        while (unvisited.isNotEmpty()) {
            val current = unvisited.minByOrNull { distances[it] ?: Double.POSITIVE_INFINITY }
                ?: break

            if (current == to) break

            unvisited.remove(current)

            adjacencyList[current]?.forEach { edge ->
                val newDist = (distances[current] ?: Double.POSITIVE_INFINITY) + edge.weight
                if (newDist < (distances[edge.destination] ?: Double.POSITIVE_INFINITY)) {
                    distances[edge.destination] = newDist
                    previous[edge.destination] = current
                }
            }
        }

        if (distances[to] == Double.POSITIVE_INFINITY) return null

        val path = mutableListOf<T>()
        var current: T? = to
        while (current != null) {
            path.add(0, current)
            current = previous[current]
        }

        return path
    }

    fun topologicalSort(): List<T>? {
        val inDegree = mutableMapOf<T, Int>()
        adjacencyList.keys.forEach { inDegree[it] = 0 }

        adjacencyList.values.forEach { edges ->
            edges.forEach { edge ->
                inDegree[edge.destination] = (inDegree[edge.destination] ?: 0) + 1
            }
        }

        val queue = mutableListOf<T>()
        inDegree.forEach { (vertex, degree) ->
            if (degree == 0) queue.add(vertex)
        }

        val result = mutableListOf<T>()

        while (queue.isNotEmpty()) {
            val current = queue.removeAt(0)
            result.add(current)

            adjacencyList[current]?.forEach { edge ->
                val newDegree = (inDegree[edge.destination] ?: 0) - 1
                inDegree[edge.destination] = newDegree
                if (newDegree == 0) {
                    queue.add(edge.destination)
                }
            }
        }

        return if (result.size == adjacencyList.size) result else null
    }

    fun getAllVertices(): Set<T> = adjacencyList.keys.toSet()

    fun clear() {
        adjacencyList.clear()
    }
}