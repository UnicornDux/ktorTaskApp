package com.edu.com.edu.ktorapp.plugins

import com.edu.com.edu.ktorapp.model.Task
import com.edu.com.edu.ktorapp.model.TaskRepository
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.util.Collections
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets(repository: TaskRepository) {
    install(WebSockets) {
        // the contentConverter properties is set, enabling the plugin to
        // serialize objects send and received through the `kotlin.serialization`
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {

        val sessions = Collections.synchronizedList<WebSocketServerSession>(ArrayList())

        webSocket("/ws") { // websocketSession
            /*
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    outgoing.send(Frame.Text("YOU SAID: $text"))
                    if (text.equals("bye", ignoreCase = true)) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    }
                }
            }
            */
            sendAllTask(repository.allTasks())
            close(CloseReason(CloseReason.Codes.NORMAL, "All done"))
        }
        webSocket("/ws2") {
            sessions.add(this)
            sendAllTask(repository.allTasks())
            while(true){
                val newTask = receiveDeserialized<Task>()
                repository.addTask(newTask)
                for (session in sessions) {
                    session.sendSerialized(newTask)
                }
            }
        }
    }
}

private suspend fun DefaultWebSocketServerSession.sendAllTask(tasks: List<Task>) {
    for (task in tasks) {
        sendSerialized(task)
        delay(1000)
    }
}