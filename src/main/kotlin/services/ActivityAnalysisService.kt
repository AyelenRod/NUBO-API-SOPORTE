package com.soporte.services

import com.soporte.datastructures.*
import com.soporte.models.*
import java.util.*

/**
 * Servicio que utiliza ARREGLOS y LISTA para análisis de actividades
 */
class ActivityAnalysisService {
    // ARREGLO DINÁMICO: Para almacenar progresos
    private val progressArray = DynamicProgressArray<StudentProgress>()

    // ARREGLO CIRCULAR: Para mantener historial de intentos recientes
    private val recentAttemptsArray = CircularArray<StudentProgress>(100)

    // LISTA ENLAZADA: Para mantener análisis ordenados
    private val analysisCache = CustomLinkedList<ActivityAnalysis>()

    fun addProgress(progress: StudentProgress) {
        progressArray.add(progress)
        recentAttemptsArray.add(progress)
    }

    fun analyzeActivity(activityId: String): ActivityAnalysis {
        val allProgress = progressArray.toList()
        val activityProgress = allProgress.filter { it.activityId == activityId }

        if (activityProgress.isEmpty()) {
            return ActivityAnalysis(
                activityId = activityId,
                totalAttempts = 0,
                averageScore = 0.0,
                averageTimeSpent = 0,
                completionRate = 0.0,
                difficultyLevel = "Unknown",
                topPerformingStudents = emptyList(),
                strugglingStudents = emptyList()
            )
        }

        val totalAttempts = activityProgress.size
        val averageScore = activityProgress.map { it.score }.average()
        val averageTimeSpent = activityProgress.map { it.timeSpent }.average().toLong()

        val completedStudents = activityProgress.filter { it.score >= 70.0 }.size
        val completionRate = (completedStudents.toDouble() / totalAttempts) * 100

        val difficultyLevel = when {
            averageScore >= 80.0 -> "Fácil"
            averageScore >= 60.0 -> "Medio"
            else -> "Difícil"
        }

        val studentScores = activityProgress.groupBy { it.studentId }
            .mapValues { it.value.map { p -> p.score }.average() }

        val topPerformingStudents = studentScores.entries
            .sortedByDescending { it.value }
            .take(5)
            .map { it.key }

        val strugglingStudents = studentScores.entries
            .filter { it.value < 50.0 }
            .map { it.key }

        val analysis = ActivityAnalysis(
            activityId = activityId,
            totalAttempts = totalAttempts,
            averageScore = averageScore,
            averageTimeSpent = averageTimeSpent,
            completionRate = completionRate,
            difficultyLevel = difficultyLevel,
            topPerformingStudents = topPerformingStudents,
            strugglingStudents = strugglingStudents
        )

        // Guardar en caché usando lista enlazada
        analysisCache.addFirst(analysis)

        return analysis
    }

    fun getRecentAttempts(count: Int = 10): List<StudentProgress> {
        val recent = recentAttemptsArray.toList()
        return recent.takeLast(minOf(count, recent.size))
    }

    fun getAllAnalyses(): List<ActivityAnalysis> {
        return analysisCache.toList()
    }

    fun clearData() {
        progressArray.clear()
        analysisCache.clear()
    }

    // Método para generar datos de prueba
    fun generateSampleData() {
        val activityIds = listOf("act1", "act2", "act3", "act4", "act5")
        val studentIds = listOf("st1", "st2", "st3", "st4", "st5", "st6", "st7", "st8")

        repeat(50) {
            val progress = StudentProgress(
                studentId = studentIds.random(),
                activityId = activityIds.random(),
                score = (30.0..100.0).random(),
                completedAt = Date(),
                timeSpent = (60L..600L).random(),
                attempts = (1..5).random()
            )
            addProgress(progress)
        }
    }
}
