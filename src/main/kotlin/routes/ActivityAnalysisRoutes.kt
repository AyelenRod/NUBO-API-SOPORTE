package com.soporte.routes

import com.soporte.services.ActivityAnalysisService
import com.soporte.models.StudentProgress
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.activityAnalysisRoutes(service: ActivityAnalysisService) {
    route("/analysis") {

        // Agregar progreso de estudiante
        post("/progress") {
            try {
                val progress = call.receive<StudentProgress>()
                service.addProgress(progress)
                call.respond(HttpStatusCode.Created, mapOf("message" to "Progreso agregado exitosamente"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // Analizar una actividad específica
        get("/activity/{activityId}") {
            try {
                val activityId = call.parameters["activityId"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "activityId requerido"))

                val analysis = service.analyzeActivity(activityId)
                call.respond(HttpStatusCode.OK, analysis)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // Obtener intentos recientes
        get("/recent-attempts") {
            try {
                val count = call.request.queryParameters["count"]?.toIntOrNull() ?: 10
                val recent = service.getRecentAttempts(count)
                call.respond(HttpStatusCode.OK, mapOf("recentAttempts" to recent))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // Obtener todos los análisis en caché
        get("/all") {
            try {
                val analyses = service.getAllAnalyses()
                call.respond(HttpStatusCode.OK, mapOf("analyses" to analyses))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // Generar datos de prueba
        post("/generate-sample") {
            try {
                service.generateSampleData()
                call.respond(HttpStatusCode.OK, mapOf("message" to "Datos de prueba generados"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // Limpiar datos
        delete("/clear") {
            try {
                service.clearData()
                call.respond(HttpStatusCode.OK, mapOf("message" to "Datos limpiados"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
    }
}