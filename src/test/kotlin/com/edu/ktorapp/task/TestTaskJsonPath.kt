package com.edu.ktorapp.task

import com.edu.com.edu.ktorapp.module
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class TestTaskJsonPath {
    @Test
    fun taskCanBeFound() = testApplication {
        application{
            module()
        }
    }


}