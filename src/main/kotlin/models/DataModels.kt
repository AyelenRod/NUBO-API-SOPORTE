package com.soporte.models

import java.util.*

// Modelo para actividad
data class Activity(
    val id: String,
    val teacherId: String,
    val moduleId: String,
    val title: String,
    val thumbnail: String,
    val isPublic: Boolean,
    val content: List<ContentItem>
)

data class ContentItem(
    val id: String,
    val texto: String,
    val imagenUrl: String,
    val silabas: List<String>,
    val grafemas: List<String>
)

// Modelo para estudiante
data class Student(
    val id: String,
    val teacherId: String,
    val nombre: String,
    val apellidoP: String,
    val apellidoM: String
)

// Modelo para progreso del estudiante
data class StudentProgress(
    val studentId: String,
    val activityId: String,
    val score: Double,
    val completedAt: Date,
    val timeSpent: Long,
    val attempts: Int
)

// Modelo para análisis de actividad
data class ActivityAnalysis(
    val activityId: String,
    val totalAttempts: Int,
    val averageScore: Double,
    val averageTimeSpent: Long,
    val completionRate: Double,
    val difficultyLevel: String,
    val topPerformingStudents: List<String>,
    val strugglingStudents: List<String>
)

// Modelo para recomendación
data class Recommendation(
    val studentId: String,
    val recommendedActivities: List<String>,
    val reasoning: String,
    val priority: Int
)

// Modelo de grafo para relaciones entre actividades
data class ActivityNode(
    val activityId: String,
    val title: String,
    val moduleId: String,
    val difficulty: Int
)

data class ActivityEdge(
    val from: String,
    val to: String,
    val weight: Double,
    val relation: String
)

// Modelo para estadísticas del maestro
data class TeacherStats(
    val teacherId: String,
    val totalActivities: Int,
    val totalStudents: Int,
    val averageStudentProgress: Double,
    val mostPopularActivity: String?,
    val leastEngagingActivity: String?,
    val studentRanking: List<StudentRank>
)

data class StudentRank(
    val studentId: String,
    val fullName: String,
    val averageScore: Double,
    val totalActivitiesCompleted: Int,
    val rank: Int
)