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
        port = System.getenv("SUPPORT_PORT")?.toInt() ?: 8080,
        host = "0.0.0.0",
        module = Application::supportModule
    ).start(wait = true)
}

fun Application.supportModule() {
    configureSupportCORS()
    configureSupportSerialization()

    // Inicializar servicios
    val activityAnalysisService = ActivityAnalysisService()
    val recommendationService = RecommendationService()
    val studentProgressService = StudentProgressService()
    val activityGraphService = ActivityGraphService()
    val teacherStatsService = TeacherStatsService()

    routing {
        get("/") {
            call.respondText("NUBO Support API - Sistema de Análisis y Recomendaciones")
        }

        get("/health") {
            call.respond(mapOf("status" to "OK", "service" to "Nubo Support API"))
        }

        // Rutas de soporte
        route("/api/support") {
            activityAnalysisRoutes(activityAnalysisService)
            recommendationRoutes(recommendationService)
            studentProgressRoutes(studentProgressService)
            activityGraphRoutes(activityGraphService)
            teacherStatsRoutes(teacherStatsService)
        }
    }
}

fun Application.configureSupportCORS() {
    install(CORS) {
        // Permitir solicitudes del API principal
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
}

fun Application.configureSupportSerialization() {
    install(ContentNegotiation) {
        jackson {
            enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT)
            enable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }
}