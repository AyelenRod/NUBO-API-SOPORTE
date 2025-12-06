package com.soporte.routes

import com.soporte.services.ActivityGraphService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.activityGraphRoutes(service: ActivityGraphService) {
    route("/graph") {

        // Agregar actividad al grafo
        post("/activity") {
            try {
                val body = call.receive<Map<String, String>>()
                val activityId = body["activityId"]
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "activityId requerido"))

                service.addActivity(activityId)
                call.respond(HttpStatusCode.Created, mapOf("message" to "Actividad agregada al grafo"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // Agregar prerrequisito
        post("/prerequisite") {
            try {
                val body = call.receive<Map<String, Any>>()
                val prerequisite = body["prerequisite"] as? String
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "prerequisite requerido"))
                val activity = body["activity"] as? String
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "activity requerido"))
                val weight = (body["weight"] as? Number)?.toDouble() ?: 1.0

                service.addPrerequisite(prerequisite, activity, weight)
                call.respond(HttpStatusCode.Created, mapOf("message" to "Prerrequisito agregado"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // Verificar si puede acceder a actividad
        post("/can-access") {
            try {
                val body = call.receive<Map<String, Any>>()
                @Suppress("UNCHECKED_CAST")
                val completedActivities = body["completedActivities"] as? List<String> ?: emptyList()
                val targetActivity = body["targetActivity"] as? String
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "targetActivity requerido"))

                val canAccess = service.canAccessActivity(completedActivities, targetActivity)
                call.respond(HttpStatusCode.OK, mapOf("canAccess" to canAccess))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // Obtener ruta recomendada
        post("/recommended-path") {
            try {
                val body = call.receive<Map<String, Any>>()
                @Suppress("UNCHECKED_CAST")
                val completedActivities = body["completedActivities"] as? List<String> ?: emptyList()
                val targetActivity = body["targetActivity"] as? String
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "targetActivity requerido"))

                val path = service.getRecommendedPath(completedActivities, targetActivity)
                call.respond(HttpStatusCode.OK, path)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // Obtener siguientes actividades sugeridas
        post("/suggested-next") {
            try {
                val body = call.receive<Map<String, Any>>()
                @Suppress("UNCHECKED_CAST")
                val completedActivities = body["completedActivities"] as? List<String> ?: emptyList()

                val suggested = service.getSuggestedNextActivities(completedActivities)
                call.respond(HttpStatusCode.OK, mapOf("suggestedActivities" to suggested))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // Obtener ruta de aprendizaje completa
        get("/learning-path") {
            try {
                val path = service.getLearningPath()
                if (path != null) {
                    call.respond(HttpStatusCode.OK, mapOf("learningPath" to path))
                } else {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "El grafo contiene ciclos"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // Obtener dependencias de una actividad
        get("/dependencies/{activityId}") {
            try {
                val activityId = call.parameters["activityId"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "activityId requerido"))

                val dependencies = service.getActivityDependencies(activityId)
                call.respond(HttpStatusCode.OK, mapOf("dependencies" to dependencies))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // Generar grafo de prueba
        post("/generate-sample") {
            try {
                service.generateSampleGraph()
                call.respond(HttpStatusCode.OK, mapOf("message" to "Grafo de prueba generado"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // Limpiar grafo
        delete("/clear") {
            try {
                service.clearGraph()
                call.respond(HttpStatusCode.OK, mapOf("message" to "Grafo limpiado"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
    }
}