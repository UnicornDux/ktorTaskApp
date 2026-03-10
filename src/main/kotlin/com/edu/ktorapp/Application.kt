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
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.CommandLineConfig
import io.ktor.server.engine.EngineConnectorBuilder
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.loadCommonConfiguration
import io.ktor.server.netty.EngineMain
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun main(args: Array<String>) {
    EngineMain.main(args)

    // embed Server with code configuration
    /**
    embeddedServer(Netty, configure =  {
        // return a connector
        connectors.add(EngineConnectorBuilder().apply {
            port = 8081
            host = "192.168.190.20"
        }),
        connectionGroupSize = 2
        workerGroupSize = 5
        callGroupSize = 10
        shutdownGracePeriod = 2000
        shutdownTimeout = 3000
    }) {
        routing {
            get("/") {
                call.respondText("hello world")
            }
        }
    }.start(wait = true)
    */

    //  with NettyApplicationEngine custom configuration

    /**
    embeddedServer(Netty, configure = {
        requestQueueLimit = 16
        shareWorkGroup = false
        configureBootstrap = {
           // ...
        }
        responseWriteTimeoutSeconds = 10
    })
    */

    // The example below shows how to run a server with multiple connectors endpoints using a
    // custom configuration represented by the ApplicationEngine.Configuration class

    /*

    val appProperties = serverConfig {
        module { module() }
    }
    embeddedServer(Netty, appProperties) {
        envConfig()
    }.start(true)

    */

    // commandline arguments
    // ./gradlew run --args="-port=8080"
    /*
    embeddedServer(
        factory = Netty,
        configure = {
            val cliConfig = CommandLineConfig(args)
            takeFrom(cliConfig.engineConfig)
            loadCommonConfiguration(cliConfig.rootConfig.environment.config)
        }
    ){
        routing {
            get ("/") {
                call.respondText("Hello")
            }
        }
    }.start(wait = true)

    */
}

// extension function
fun ApplicationEngine.Configuration.envConfig() {
    connector {
        host = "0.0.0.0"
        port = 8080
    }
    connector {
        host = "127.0.0.1"
        port = 9090
    }
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
