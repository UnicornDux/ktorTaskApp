package com.edu.ktorapp.task

import com.edu.com.edu.ktorapp.model.Priority
import com.edu.com.edu.ktorapp.model.Task
import com.edu.com.edu.ktorapp.module
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.DocumentContext
import io.ktor.client.HttpClient
import io.ktor.client.request.accept

class TaskTest {

    /**
     * The testApplication() method creates a new instance of Ktor. This
     * instance is running inside a test environment, as opposed to a sever such as netty
     */
    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.Companion.OK, status)
        }
    }


    @Test
    fun testHtml() = testApplication {
        application {
            module()
        }
        client.get("/index").apply {
            val response = client.get("/index")
            assertEquals(HttpStatusCode.Companion.OK, response.status)
            assertEquals("html", response.contentType()?.contentSubtype)
            assertContains(response.bodyAsText(), "Hello from Ktor")
        }
    }


    @Test
    fun tasksCanBeFoundByPriority() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.get("/tasks/byPriority/Medium")
        val body = response.body<List<Task>>()
        assertEquals(HttpStatusCode.Companion.OK, response.status)
        val expectedTaskNames = listOf("Task 2")
        val actualTaskNames = body.map(Task::name)
        assertContentEquals(expectedTaskNames, actualTaskNames)
    }

    @Test
    fun invalidPriorityProduces400() = testApplication {
        application {
            module()
        }

        val response = client.get("/tasks/byPriority/Invalud")
        assertEquals(HttpStatusCode.Companion.BadRequest, response.status)
    }

    @Test
    fun unusedPriorityProduces404() = testApplication {
        application {
            module()
        }

        val response = client.get("/tasks/byPriority/Vital")
        assertEquals(HttpStatusCode.Companion.NotFound, response.status)
    }


    @Test
    fun newTasksCanBeAdded() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val task = Task("swimming", "Go to the Beach", Priority.Low)
        val response1 = client.post("tasks") {
            header(
                HttpHeaders.ContentType,
                // ContentType.Application.FormUrlEncoded.toString()
                ContentType.Application.Json
            )
            /** url encoding parameters
            setBody(
                listOf(
                    "name" to "swimming",
                    "description" to "Go to the Beach",
                    "priority" to "Low"
                ).formUrlEncode()
            )
            */

            // json parameters
            setBody(task)
        }

        assertEquals(HttpStatusCode.Companion.Created, response1.status)
        val response2 = client.get("tasks")
        assertEquals(HttpStatusCode.Companion.OK, response2.status)
        val taskNames = response2.body<List<Task>>().map { it.name }

        assertContains(taskNames, "swimming")
    }


    // json path to test json response
    @Test
    fun taskCanBeFoundValidJsonPath() = testApplication {
        application {
            module()
        }
        val jsonDoc = client.getAsJsonPath("/tasks")
        // $[*].name means "treat the document as an array and return the
        // value of the name property for each entry
        val result: List<String> = jsonDoc.read("$[*].name")
        assertEquals("Task 1", result[0])
        assertEquals("Task 2", result[1])
        assertEquals("Task 3", result[2])
    }

    @Test
    fun tasksCanBeFoundByPriorityValidJsonPath() = testApplication {
        application {
            module()
        }
        val priority = Priority.Medium
        val jsonDoc = client.getAsJsonPath("/tasks/byPriority/$priority")
        // $[?(@.priority == 'Medium')].name means "return the value of the name property of every entry
        // in the array with a priority equal to the supplied value
        val result: List<String> = jsonDoc.read("$[?(@.priority == '$priority')].name")
        assertEquals(1, result.size)
        assertEquals("Task 2", result[0])
    }

    suspend fun HttpClient.getAsJsonPath(url: String): DocumentContext {
        val response = this.get(url) {
            accept(ContentType.Application.Json)
        }
        return JsonPath.parse(response.bodyAsText())
    }
}
