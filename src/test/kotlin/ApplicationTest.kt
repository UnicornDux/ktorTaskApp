package com.edu

import com.edu.com.edu.ktorapp.model.Priority
import com.edu.com.edu.ktorapp.model.Task
import com.edu.com.edu.ktorapp.module
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ApplicationTest {

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
            assertEquals(HttpStatusCode.OK, status)
        }
    }


    @Test
    fun testHtml() = testApplication {
        application {
            module()
        }
        client.get("/index").apply {
            val response = client.get("/index")
            assertEquals(HttpStatusCode.OK, response.status)
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
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedTaskNames = listOf("Task 2")
        val actualTaskNames  = body.map(Task::name)
        assertContentEquals(expectedTaskNames, actualTaskNames)
    }

    @Test
    fun invalidPriorityProduces400() = testApplication {
        application {
            module()
        }

        val response = client.get("/tasks/byPriority/Invalud")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun unusedPriorityProduces404() = testApplication {
        application{
            module()
        }

        val response = client.get("/tasks/byPriority/Vital")
        assertEquals(HttpStatusCode.NotFound,response.status)
    }


    @Test
    fun newTasksCanBeAdded() = testApplication {
        application{
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

        assertEquals(HttpStatusCode.Created, response1.status)
        val response2 = client.get("tasks")
        assertEquals(HttpStatusCode.OK, response2.status)
        val taskNames = response2.body<List<Task>>().map{ it.name }

        assertContains(taskNames, "swimming")
    }
}
