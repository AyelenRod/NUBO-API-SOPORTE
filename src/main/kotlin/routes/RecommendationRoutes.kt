package com.soporte.routes

import com.soporte.services.RecommendationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.recommendationRoutes(service: RecommendationService) {
    route("/recommendations") {

        // Registrar visita a actividad
        post("/track-visit") {
            try {
                val params = call.receive<Map<String, String>>()
                val studentId = params["studentId"]
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "studentId requerido"))
                val activityId = params["activityId"]
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "activityId requerido"))

                service.trackActivityVisit(studentId, activityId)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Visita registrada"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // Obtener historial de navegación
        get("/history/{studentId}") {
            try {
                val studentId = call.parameters["studentId"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "studentId requerido"))

                val history = service.getNavigationHistory(studentId)
                call.respond(HttpStatusCode.OK, mapOf("history" to history))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // Generar recomendación
        post("/generate") {
            try {
                val body = call.receive<Map<String, Any>>()
                val studentId = body["studentId"] as? String
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "studentId requerido"))

                @Suppress("UNCHECKED_CAST")
                val availableActivities = body["availableActivities"] as? List<String> ?: emptyList()
                @Suppress("UNCHECKED_CAST")
                val completedActivities = body["completedActivities"] as? List<String> ?: emptyList()
                val studentScore = (body["studentScore"] as? Number)?.toDouble() ?: 0.0

                val recommendation = service.generateRecommendation(
                    studentId,
                    availableActivities,
                    completedActivities,
                    studentScore
                )

                call.respond(HttpStatusCode.OK, recommendation)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // Procesar siguiente recomendación en cola
        post("/process-next") {
            try {
                val recommendation = service.processNextRecommendation()
                if (recommendation != null) {
                    call.respond(HttpStatusCode.OK, recommendation)
                } else {
                    call.respond(HttpStatusCode.NoContent)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // Obtener todas las recomendaciones pendientes
        get("/pending") {
            try {
                val pending = service.getAllPendingRecommendations()
                val count = service.getPendingRecommendationsCount()
                call.respond(HttpStatusCode.OK, mapOf(
                    "count" to count,
                    "recommendations" to pending
                ))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // Obtener actividades recomendadas para un estudiante
        get("/recommended/{studentId}") {
            try {
                val studentId = call.parameters["studentId"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "studentId requerido"))

                val recommended = service.getRecommendedActivitiesSet(studentId)
                call.respond(HttpStatusCode.OK, mapOf("recommendedActivities" to recommended))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // Limpiar recomendaciones de un estudiante
        delete("/clear/{studentId}") {
            try {
                val studentId = call.parameters["studentId"]
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "studentId requerido"))

                service.clearRecommendedActivities(studentId)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Recomendaciones limpiadas"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
    }
}