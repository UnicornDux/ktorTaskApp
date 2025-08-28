package com.edu.ktorapp.task

import com.edu.ktorapp.task.FakeTaskRepository
import com.edu.com.edu.ktorapp.model.Priority
import com.edu.com.edu.ktorapp.model.Task
import com.edu.com.edu.ktorapp.plugins.configureRouting
import com.edu.com.edu.ktorapp.plugins.configureSerialization
import com.edu.com.edu.ktorapp.plugins.configureSockets
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.converter
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.serialization.deserialize
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class WsTest {
    @Test
    fun testRoot() = testApplication {
        application {
            val repository = FakeTaskRepository()
            configureRouting(repository)
            configureSerialization(repository)
            configureSockets(repository)
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }

        val expectedTasks = listOf(
            Task("Task 1", "Description 1", Priority.Low),
            Task("Task 2", "Description 2", Priority.Medium),
            Task("Task 3", "Description 3", Priority.High),
        )

        var actualTasks = emptyList<Task>()
        client.webSocket("/ws") {
            consumeTasksAsFlow().collect { allTask ->
                actualTasks = allTask
            }
        }
        assertEquals(expectedTasks.size, actualTasks.size)
        expectedTasks.forEachIndexed { index, task ->
            assertEquals(task, actualTasks[index])
        }
    }

    private fun DefaultClientWebSocketSession.consumeTasksAsFlow() = incoming
        .consumeAsFlow()
        .map {
            converter!!.deserialize<Task>(it)
        }
        .scan(emptyList<Task>()) { list, task ->
            list + task
        }
}