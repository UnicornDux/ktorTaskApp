package com.edu.com.edu.ktorapp.plugins

import com.edu.com.edu.ktorapp.model.TaskRepository
import com.edu.com.edu.ktorapp.model.tasksAsTable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        install(StatusPages) {
            // what Action to take when an exception of type IllegalStateException is thrown

            exception<Exception> { call, cause ->
               call.respondText(text = "500: ${cause.message}", status = HttpStatusCode.InternalServerError)
            }

            exception<IllegalStateException> { call, cause ->
                call.respondText(text = "500: ${cause.message}", status = HttpStatusCode.InternalServerError)
            }
        }

        // pages is the base directory store the static file
        // Ktor will look for this folder within the resources directory
        staticResources("/content", "pages")
        get("/") {
            call.respondText("Hello World!")
        }
        get("/index") {
            val text = "<h1> Hello from Ktor</h1>"
            val type = ContentType.parse("text/html")
            call.respondText(text, type)
        }
        get("/error-test") {
            throw IllegalStateException("Test exception")
        }
        get("/error") {
            throw Exception("Test global exception")
        }

        get("/tasks") {
            val tasks = TaskRepository.allTasks()
            call.respondText(
                contentType = ContentType.parse("text/html"),
                text = tasks.tasksAsTable()
            )
        }
    }
}