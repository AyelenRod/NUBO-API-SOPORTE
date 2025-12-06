package com.soporte.services

import com.soporte.datastructures.ActivityBST
import com.soporte.models.ActivityNode

class StudentProgressService {
    // ÁRBOL: Para organizar actividades por nivel de dificultad
    private val activityTree = ActivityBST()

    fun addActivity(activityId: String, difficulty: Int, title: String) {
        activityTree.insert(activityId, difficulty, title)
    }

    fun findActivitiesByDifficultyRange(minDiff: Int, maxDiff: Int): List<Map<String, Any>> {
        val nodes = activityTree.findActivitiesByDifficultyRange(minDiff, maxDiff)
        return nodes.map { node ->
            mapOf(
                "activityId" to node.activityId,
                "difficulty" to node.difficulty,
                "title" to node.title
            )
        }
    }

    fun getActivitiesSortedByDifficulty(): List<Map<String, Any>> {
        val nodes = activityTree.inOrderTraversal()
        return nodes.map { node ->
            mapOf(
                "activityId" to node.activityId,
                "difficulty" to node.difficulty,
                "title" to node.title
            )
        }
    }

    fun suggestNextActivity(currentDifficulty: Int, direction: String = "up"): List<Map<String, Any>> {
        val range = when (direction) {
            "up" -> Pair(currentDifficulty + 1, currentDifficulty + 3)
            "down" -> Pair(maxOf(1, currentDifficulty - 3), currentDifficulty - 1)
            else -> Pair(currentDifficulty - 1, currentDifficulty + 1)
        }

        return findActivitiesByDifficultyRange(range.first, range.second)
    }

    fun clearActivities() {
        activityTree.clear()
    }

    fun generateSampleActivities() {
        val sampleActivities = listOf(
            Triple("act1", 1, "Introducción a las sílabas"),
            Triple("act2", 2, "Reconocimiento de vocales"),
            Triple("act3", 3, "Consonantes simples"),
            Triple("act4", 4, "Palabras de dos sílabas"),
            Triple("act5", 5, "Frases cortas"),
            Triple("act6", 6, "Lectura de párrafos simples"),
            Triple("act7", 7, "Comprensión lectora básica"),
            Triple("act8", 8, "Vocabulario avanzado"),
            Triple("act9", 9, "Escritura creativa"),
            Triple("act10", 10, "Análisis de textos")
        )

        sampleActivities.forEach { (id, diff, title) ->
            addActivity(id, diff, title)
        }
    }
}