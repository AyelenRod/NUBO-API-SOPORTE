package com.soporte

import com.soporte.routes.*
import com.soporte.services.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.plugins.cors.routing.*

fun main() {
    embeddedServer(
        Netty,
        port = System.getenv("PORT")?.toInt() ?: 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    // Configuración CORS
    install(CORS) {
        // Permitir solicitudes del API principal y otros hosts
        allowHost("localhost:9000", schemes = listOf("http"))
        allowHost("54.226.246.30:9000", schemes = listOf("http"))
        anyHost()

        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)

        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)

        allowCredentials = true
        maxAgeInSeconds = 3600
    }

    // Configuración de serialización
    install(ContentNegotiation) {
        jackson {
            enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT)
            enable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    // Inicializar servicios
    val activityAnalysisService = ActivityAnalysisService()
    val recommendationService = RecommendationService()
    val studentProgressService = StudentProgressService()
    val activityGraphService = ActivityGraphService()
    val teacherStatsService = TeacherStatsService()

    routing {
        // Ruta raíz
        get("/") {
            call.respondText("NUBO Support API - Sistema de Análisis y Recomendaciones")
        }

        // Health check
        get("/health") {
            call.respond(mapOf("status" to "OK", "service" to "Nubo Support API"))
        }

        // Todas las rutas bajo /api/support
        route("/api/support") {
            activityAnalysisRoutes(activityAnalysisService)
            recommendationRoutes(recommendationService)
            studentProgressRoutes(studentProgressService)
            activityGraphRoutes(activityGraphService)
            teacherStatsRoutes(teacherStatsService)
        }
    }
}