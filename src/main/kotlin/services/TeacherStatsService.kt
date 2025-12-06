package com.soporte.services

import com.soporte.datastructures.CustomHashSet
import com.soporte.models.*

class TeacherStatsService {
    private val teacherStudents = mutableMapOf<String, CustomHashSet<String>>()

    private val teacherActivities = mutableMapOf<String, CustomHashSet<String>>()

    private val allProgress = mutableListOf<StudentProgress>()

    fun addStudentToTeacher(teacherId: String, studentId: String) {
        if (!teacherStudents.containsKey(teacherId)) {
            teacherStudents[teacherId] = CustomHashSet()
        }
        teacherStudents[teacherId]?.add(studentId)
    }

    fun addActivityToTeacher(teacherId: String, activityId: String) {
        if (!teacherActivities.containsKey(teacherId)) {
            teacherActivities[teacherId] = CustomHashSet()
        }
        teacherActivities[teacherId]?.add(activityId)
    }

    fun addProgress(progress: StudentProgress) {
        allProgress.add(progress)
    }

    fun getTeacherStats(teacherId: String): TeacherStats {
        val students = teacherStudents[teacherId]?.toList() ?: emptyList()
        val activities = teacherActivities[teacherId]?.toList() ?: emptyList()

        // Calcular progreso promedio de estudiantes
        val studentProgresses = allProgress.filter { it.studentId in students }
        val averageProgress = if (studentProgresses.isNotEmpty()) {
            studentProgresses.map { it.score }.average()
        } else 0.0

        // Encontrar actividad más popular
        val activityAttempts = studentProgresses
            .groupBy { it.activityId }
            .mapValues { it.value.size }
        val mostPopular = activityAttempts.maxByOrNull { it.value }?.key
        val leastEngaging = activityAttempts.minByOrNull { it.value }?.key

        // Crear ranking de estudiantes
        val studentScores = studentProgresses
            .groupBy { it.studentId }
            .mapValues { entry ->
                val scores = entry.value.map { it.score }
                val avgScore = scores.average()
                val completed = entry.value.distinctBy { it.activityId }.size
                Pair(avgScore, completed)
            }

        val ranking = studentScores.entries
            .sortedByDescending { it.value.first }
            .mapIndexed { index, entry ->
                StudentRank(
                    studentId = entry.key,
                    fullName = "Estudiante ${entry.key}",
                    averageScore = entry.value.first,
                    totalActivitiesCompleted = entry.value.second,
                    rank = index + 1
                )
            }

        return TeacherStats(
            teacherId = teacherId,
            totalActivities = activities.size,
            totalStudents = students.size,
            averageStudentProgress = averageProgress,
            mostPopularActivity = mostPopular,
            leastEngagingActivity = leastEngaging,
            studentRanking = ranking
        )
    }

    fun getCommonStudents(teacherId1: String, teacherId2: String): List<String> {
        val students1 = teacherStudents[teacherId1] ?: return emptyList()
        val students2 = teacherStudents[teacherId2] ?: return emptyList()

        return students1.intersection(students2).toList()
    }

    fun getAllStudents(teacherIds: List<String>): List<String> {
        var result = CustomHashSet<String>()

        teacherIds.forEach { teacherId ->
            teacherStudents[teacherId]?.let { students ->
                result = result.union(students)
            }
        }

        return result.toList()
    }

    fun clearData() {
        teacherStudents.clear()
        teacherActivities.clear()
        allProgress.clear()
    }

    fun generateSampleData() {
        val teacher1 = "teacher1"
        val teacher2 = "teacher2"

        repeat(10) { i ->
            addStudentToTeacher(teacher1, "student${i}")
        }

        repeat(8) { i ->
            addStudentToTeacher(teacher2, "student${i + 5}")
        }

        // Agregar actividades
        repeat(5) { i ->
            addActivityToTeacher(teacher1, "act${i}")
        }

        repeat(4) { i ->
            addActivityToTeacher(teacher2, "act${i + 3}")
        }

        // Generar progreso aleatorio
        teacherStudents[teacher1]?.toList()?.forEach { studentId ->
            teacherActivities[teacher1]?.toList()?.forEach { activityId ->
                addProgress(
                    StudentProgress(
                        studentId = studentId,
                        activityId = activityId,
                        score = (50..100).random().toDouble(),
                        completedAt = java.util.Date(),
                        timeSpent = (60L..600L).random(),
                        attempts = (1..3).random()
                    )
                )
            }
        }
    }
}