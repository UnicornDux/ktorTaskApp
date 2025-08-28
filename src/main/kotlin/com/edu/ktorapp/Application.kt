package com.edu.com.edu.ktorapp

import com.edu.com.edu.ktorapp.model.PostgresTaskRepository
import com.edu.com.edu.ktorapp.plugins.configureDatabases
import com.edu.com.edu.ktorapp.plugins.configureFrameworks
import com.edu.com.edu.ktorapp.plugins.configureHTTP
import com.edu.com.edu.ktorapp.plugins.configureRouting
import com.edu.com.edu.ktorapp.plugins.configureSerialization
import com.edu.com.edu.ktorapp.plugins.configureSockets
import com.edu.com.edu.ktorapp.plugins.configureTemplating
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
   EngineMain.main(args)
}

fun Application.module() {
    val repository = PostgresTaskRepository()

    configureHTTP()
    configureSerialization(repository)
    configureDatabases()
    configureTemplating(repository)
    configureFrameworks()
    configureSockets(repository)
    configureRouting(repository)
}
