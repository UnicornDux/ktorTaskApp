package com.edu

import com.edu.com.edu.ktorapp.module
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertContains
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
}
