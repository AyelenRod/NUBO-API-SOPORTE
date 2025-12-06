package com.soporte.services

import com.soporte.datastructures.DirectedGraph

class ActivityGraphService {
    // GRAFO DIRIGIDO: Para representar rutas de aprendizaje y prerrequisitos
    private val activityGraph = DirectedGraph<String>()

    fun addActivity(activityId: String) {
        activityGraph.addVertex(activityId)
    }

    fun addPrerequisite(prerequisite: String, activity: String, weight: Double = 1.0) {
        activityGraph.addEdge(prerequisite, activity, weight, "prerequisite")
    }

    fun addSimilarActivity(activity1: String, activity2: String, similarity: Double) {
        activityGraph.addEdge(activity1, activity2, 1.0 - similarity, "similar")
    }

    fun addNextLevel(currentActivity: String, nextActivity: String, difficulty: Double = 1.0) {
        activityGraph.addEdge(currentActivity, nextActivity, difficulty, "next_level")
    }

    fun canAccessActivity(studentCompletedActivities: List<String>, targetActivity: String): Boolean {
        return studentCompletedActivities.any { completed ->
            activityGraph.hasPath(completed, targetActivity)
        } || studentCompletedActivities.contains(targetActivity)
    }

    fun getRecommendedPath(
        completedActivities: List<String>,
        targetActivity: String
    ): Map<String, Any> {
        if (completedActivities.isEmpty()) {
            return mapOf(
                "canAccess" to false,
                "path" to emptyList<String>(),
                "message" to "Completa algunas actividades básicas primero"
            )
        }

        // Encontrar la ruta más corta desde cualquier actividad completada
        val paths = completedActivities.mapNotNull { completed ->
            activityGraph.findShortestPath(completed, targetActivity)
        }

        val shortestPath = paths.minByOrNull { it.size }

        return if (shortestPath != null) {
            mapOf(
                "canAccess" to true,
                "path" to shortestPath,
                "steps" to shortestPath.size - 1,
                "message" to "Sigue esta ruta de aprendizaje"
            )
        } else {
            mapOf(
                "canAccess" to false,
                "path" to emptyList<String>(),
                "message" to "No hay ruta disponible desde tus actividades completadas"
            )
        }
    }

    fun getSuggestedNextActivities(completedActivities: List<String>): List<String> {
        val nextActivities = mutableSetOf<String>()

        completedActivities.forEach { completed ->
            val neighbors = activityGraph.getNeighbors(completed)
            neighbors.forEach { edge ->
                nextActivities.add(edge.destination)
            }
        }

        // Filtrar actividades ya completadas
        return nextActivities.filterNot { it in completedActivities }
    }

    fun getLearningPath(): List<String>? {
        // Obtener orden topológico de todas las actividades
        return activityGraph.topologicalSort()
    }

    fun getActivityDependencies(activityId: String): List<String> {
        val allVertices = activityGraph.getAllVertices()
        return allVertices.filter { vertex ->
            activityGraph.hasPath(vertex, activityId) && vertex != activityId
        }
    }

    fun clearGraph() {
        activityGraph.clear()
    }

    fun generateSampleGraph() {
        val activities = listOf("act1", "act2", "act3", "act4", "act5", "act6", "act7", "act8")
        activities.forEach { addActivity(it) }

        // Crear dependencias (prerequisitos)
        addPrerequisite("act1", "act2")
        addPrerequisite("act1", "act3")
        addPrerequisite("act2", "act4")
        addPrerequisite("act3", "act4")
        addPrerequisite("act4", "act5")
        addPrerequisite("act4", "act6")
        addPrerequisite("act5", "act7")
        addPrerequisite("act6", "act7")
        addPrerequisite("act7", "act8")

        // Agregar actividades similares
        addSimilarActivity("act2", "act3", 0.8)
        addSimilarActivity("act5", "act6", 0.7)
    }
}