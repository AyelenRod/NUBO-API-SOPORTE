package com.soporte.services

import com.soporte.datastructures.*
import com.soporte.models.*

/**
 * Servicio que utiliza PILA y COLA para manejar recomendaciones
 */
class RecommendationService {
    // PILA: Para mantener historial de navegación de actividades
    private val navigationHistory = mutableMapOf<String, CustomStack<String>>()

    // COLA: Para procesar recomendaciones pendientes
    private val recommendationQueue = CustomQueue<Recommendation>()

    // CONJUNTO: Para mantener actividades ya recomendadas
    private val recommendedActivities = mutableMapOf<String, CustomHashSet<String>>()

    fun trackActivityVisit(studentId: String, activityId: String) {
        if (!navigationHistory.containsKey(studentId)) {
            navigationHistory[studentId] = CustomStack()
        }
        navigationHistory[studentId]?.push(activityId)
    }

    fun getNavigationHistory(studentId: String): List<String> {
        return navigationHistory[studentId]?.toList() ?: emptyList()
    }

    fun generateRecommendation(
        studentId: String,
        availableActivities: List<String>,
        completedActivities: List<String>,
        studentScore: Double
    ): Recommendation {
        // Obtener historial de navegación
        val history = navigationHistory[studentId]?.toList() ?: emptyList()

        // Filtrar actividades no completadas y no recomendadas previamente
        val studentRecommended = recommendedActivities[studentId] ?: CustomHashSet()
        val candidates = availableActivities.filter { activityId ->
            activityId !in completedActivities && !studentRecommended.contains(activityId)
        }

        val recommended = when {
            studentScore >= 80.0 -> {
                // Estudiante avanzado: recomendar actividades desafiantes
                candidates.take(3)
            }
            studentScore >= 60.0 -> {
                // Estudiante intermedio: mezcla de actividades
                candidates.take(4)
            }
            else -> {
                // Estudiante que necesita refuerzo: actividades básicas
                candidates.take(5)
            }
        }

        val priority = when {
            studentScore < 50.0 -> 1 // Alta prioridad
            studentScore < 70.0 -> 2 // Media prioridad
            else -> 3 // Baja prioridad
        }

        val reasoning = when {
            studentScore >= 80.0 -> "Excelente desempeño. Te recomendamos actividades más desafiantes."
            studentScore >= 60.0 -> "Buen progreso. Continúa con actividades de nivel intermedio."
            else -> "Necesitas más práctica. Te sugerimos reforzar conceptos básicos."
        }

        val recommendation = Recommendation(
            studentId = studentId,
            recommendedActivities = recommended,
            reasoning = reasoning,
            priority = priority
        )

        // Agregar a la cola de procesamiento
        recommendationQueue.enqueue(recommendation)

        // Marcar actividades como recomendadas
        if (!recommendedActivities.containsKey(studentId)) {
            recommendedActivities[studentId] = CustomHashSet()
        }
        recommended.forEach { recommendedActivities[studentId]?.add(it) }

        return recommendation
    }

    fun processNextRecommendation(): Recommendation? {
        return recommendationQueue.dequeue()
    }

    fun getPendingRecommendationsCount(): Int {
        return recommendationQueue.size()
    }

    fun getAllPendingRecommendations(): List<Recommendation> {
        return recommendationQueue.toList()
    }

    fun clearRecommendedActivities(studentId: String) {
        recommendedActivities[studentId]?.clear()
    }

    fun getRecommendedActivitiesSet(studentId: String): List<String> {
        return recommendedActivities[studentId]?.toList() ?: emptyList()
    }
}