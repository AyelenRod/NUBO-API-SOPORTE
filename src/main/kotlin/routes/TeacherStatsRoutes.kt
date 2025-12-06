package com.soporte.routes

import com.soporte.services.TeacherStatsService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.teacherStatsRoutes(service: TeacherStatsService) {
    route("/teacher-stats") {

        // Agregar estudiante a maestro
        post("/add-student") {
            try {
                val body = call.receive<Map<String, String>>()
                val teacherId = body["teacherId"]
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "teacherId requerido"))
                val studentId = body["studentId"]
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "studentId requerido"))

                service.addStudentToTeacher(teacherId, studentId)
                call.respond(HttpStatusCode.Created, mapOf("message" to "Estudiante agregado"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // Agregar actividad a maestro
        post("/add-activity") {
            try {
                val body = call.receive<Map<String, String>>()
                val teacherId = body["teacherId"]
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "teacherId requerido"))
                val activityId = body["activityId"]
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "activityId requerido"))

                service.addActivityToTeacher(teacherId, activityId)
                call.respond(HttpStatusCode.Created, mapOf("message" to "Actividad agregada"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // Obtener estadísticas de un maestro
        get("/{teacherId}") {
            try {
                val teacherId = call.parameters["teacherId"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "teacherId requerido"))

                val stats = service.getTeacherStats(teacherId)
                call.respond(HttpStatusCode.OK, stats)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // Obtener estudiantes en común entre dos maestros
        get("/common-students") {
            try {
                val teacherId1 = call.request.queryParameters["teacher1"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "teacher1 requerido"))
                val teacherId2 = call.request.queryParameters["teacher2"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "teacher2 requerido"))

                val commonStudents = service.getCommonStudents(teacherId1, teacherId2)
                call.respond(HttpStatusCode.OK, mapOf("commonStudents" to commonStudents))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // Obtener todos los estudiantes de varios maestros (unión)
        post("/all-students") {
            try {
                val body = call.receive<Map<String, Any>>()
                @Suppress("UNCHECKED_CAST")
                val teacherIds = body["teacherIds"] as? List<String> ?: emptyList()

                val allStudents = service.getAllStudents(teacherIds)
                call.respond(HttpStatusCode.OK, mapOf("allStudents" to allStudents))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
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