package com.soporte.routes

import com.soporte.services.StudentProgressService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.studentProgressRoutes(service: StudentProgressService) {
    route("/progress") {

        // Agregar actividad al árbol
        post("/activity") {
            try {
                val body = call.receive<Map<String, Any>>()
                val activityId = body["activityId"] as? String
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "activityId requerido"))
                val difficulty = (body["difficulty"] as? Number)?.toInt()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "difficulty requerido"))
                val title = body["title"] as? String
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "title requerido"))

                service.addActivity(activityId, difficulty, title)
                call.respond(HttpStatusCode.Created, mapOf("message" to "Actividad agregada"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // Buscar actividades por rango de dificultad
        get("/difficulty-range") {
            try {
                val minDiff = call.request.queryParameters["min"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "min requerido"))
                val maxDiff = call.request.queryParameters["max"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "max requerido"))

                val activities = service.findActivitiesByDifficultyRange(minDiff, maxDiff)
                call.respond(HttpStatusCode.OK, mapOf("activities" to activities))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // Obtener actividades ordenadas por dificultad
        get("/sorted") {
            try {
                val activities = service.getActivitiesSortedByDifficulty()
                call.respond(HttpStatusCode.OK, mapOf("activities" to activities))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // Sugerir siguiente actividad
        get("/suggest-next") {
            try {
                val currentDiff = call.request.queryParameters["currentDifficulty"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "currentDifficulty requerido"))
                val direction = call.request.queryParameters["direction"] ?: "up"

                val suggestions = service.suggestNextActivity(currentDiff, direction)
                call.respond(HttpStatusCode.OK, mapOf("suggestions" to suggestions))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // Generar actividades de prueba
        post("/generate-sample") {
            try {
                service.generateSampleActivities()
                call.respond(HttpStatusCode.OK, mapOf("message" to "Actividades de prueba generadas"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // Limpiar actividades
        delete("/clear") {
            try {
                service.clearActivities()
                call.respond(HttpStatusCode.OK, mapOf("message" to "Actividades limpiadas"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
    }
}