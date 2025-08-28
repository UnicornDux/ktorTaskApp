package com.edu.com.edu.ktorapp.plugins

import com.edu.com.edu.ktorapp.model.TaskRepository
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSerialization(repository: TaskRepository) {
    // The Content Negotiation plugin needs to find a format to send back to
    // the browser. With this configuration, also configure the Kotlinx.Serialization
    // plugin
    install(ContentNegotiation) {
        // in the case of request from the browser the ContentNegotiation plugin known
        // it can only return JSON. and the browser will try to display anything it's
        // sent, So the Request succeed.
        json()
    }


    routing {
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}
