package com.edu.com.edu.ktorapp.plugins

import com.edu.com.edu.ktorapp.model.Priority
import com.edu.com.edu.ktorapp.model.Task
import com.edu.com.edu.ktorapp.model.TaskRepository
import com.edu.com.edu.ktorapp.model.tasksAsTable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException

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

        // merge same routes
        route("/tasks") {
            get {
                val tasks = TaskRepository.allTasks()
                call.respond(tasks)
            }

            get("/byPriority/{priority?}") {
                // path parameters
                val priorityAsText = call.parameters["priority"]
                if (priorityAsText == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                try {
                    val priority = Priority.valueOf(priorityAsText)
                    val tasks = TaskRepository.tasksByPriority(priority)
                    if (tasks.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    call.respond( tasks )
                }catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            get("/byName/{taskName}") {
                val name = call.parameters["taskName"]
                if (name == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val task = TaskRepository.taskByName(name)
                if (task == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(task)
            }

            /*
            post {
                // handle the form parameters
                val formContent = call.receiveParameters()
                val params = Triple(
                    formContent["name"] ?: "",
                    formContent["description"] ?: "",
                    formContent["priority"] ?: ""
                )
                if (params.toList().any {it.isEmpty()}) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                try {
                    val priority = Priority.valueOf(params.third)
                    TaskRepository.addTask(
                        Task(
                            params.first,
                            params.second,
                            priority
                        )
                    )
                    call.respond(HttpStatusCode.NoContent)
                }catch(ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }catch(ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            */
            post {
                try {
                    val task = call.receive<Task>()
                    TaskRepository.addTask(task)
                    call.respond(HttpStatusCode.Created)
                }catch(ex: IllegalStateException){
                    call.respond(HttpStatusCode.BadRequest)
                }catch(ex: SerializationException){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            delete("/{taskName}") {
                val name = call.parameters["taskName"]
                if (name == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                if (TaskRepository.removeTask(name)) {
                     call.respond(HttpStatusCode.NoContent)
                }else {
                     call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}