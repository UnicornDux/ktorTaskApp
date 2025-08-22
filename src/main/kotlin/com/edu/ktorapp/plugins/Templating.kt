package com.edu.com.edu.ktorapp.plugins

import com.edu.com.edu.ktorapp.model.Priority
import com.edu.com.edu.ktorapp.model.Task
import com.edu.com.edu.ktorapp.model.TaskRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receive
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

fun Application.configureTemplating() {
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/thymeleaf/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }
    routing {
        staticResources("/static", "static")
        route("/todos") {
            get {
                val tasks = TaskRepository.allTasks()
                call.respond(ThymeleafContent("all-todos", mapOf("tasks" to tasks)))
            }
            get("byName") {
                val name = call.request.queryParameters["name"]
                if (name == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val task = TaskRepository.taskByName(name)
                if (task == null){
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(
                    ThymeleafContent("todo-by-name", mapOf("task" to task))
                )
            }
            get("byPriority") {
                val priorityText = call.request.queryParameters["priority"]
                if (priorityText == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                try {
                    val priority = Priority.valueOf(priorityText)
                    val tasks = TaskRepository.tasksByPriority(priority)
                    if (tasks.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    val data = mapOf(
                        "priority" to priority,
                        "tasks" to tasks
                    )
                    call.respond(
                        ThymeleafContent("todo-by-priority", data)
                    )
                }catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post {
                val formContent = call.receiveParameters()
                val params = Triple(
                    formContent["name"] ?: "",
                    formContent["description"] ?: "",
                    formContent["priority"] ?: "",
                )

                if (params.toList().any{ it.isEmpty()}) {
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
                    val tasks = TaskRepository.allTasks()
                    call.respond(ThymeleafContent("all-todos", mapOf("tasks" to tasks)))
                }catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }catch (e: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
        get("/html-thymeleaf") {
            call.respond(
                ThymeleafContent(
                    "index",
                    mapOf("user" to ThymeleafUser(1, "user1"))
                )
            )
        }
    }
}

data class ThymeleafUser(val id: Int, val name: String)
