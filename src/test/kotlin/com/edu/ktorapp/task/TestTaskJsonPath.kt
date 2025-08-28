package com.edu.ktorapp.task

import com.edu.com.edu.ktorapp.model.Priority
import com.edu.com.edu.ktorapp.module
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath.*
import io.ktor.client.HttpClient
import io.ktor.http.ContentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

class TestTaskJsonPath {

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
        return parse(response.bodyAsText())
    }

}