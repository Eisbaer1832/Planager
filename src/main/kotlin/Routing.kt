package com.capputinodevelopment

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.client.HttpClient
import java.util.Base64

fun Application.configureRouting() {
    routing {
        get("{fullPath...}") {
            // Get the full path segments
            val pathSegments = call.parameters.getAll("fullPath") ?: emptyList()
            val path = pathSegments.joinToString("/")
            val username = call.request.queryParameters["username"] ?: ""
            val password = call.request.queryParameters["password"] ?: ""

            val client = HttpClient()            
            val authString = "$username:$password"
            val encodedAuth = Base64.getEncoder().encodeToString(authString.toByteArray())
            println(path)
            val data =client.get("https://www.stundenplan24.de/$path") {
                        headers.append(HttpHeaders.Authorization, "Basic $encodedAuth")
            }.bodyAsText()

            call.respondText(data)
        }
    }
}
