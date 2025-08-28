package com.edu.ktorapp.task

import com.edu.com.edu.ktorapp.model.Priority
import com.edu.com.edu.ktorapp.model.Task
import com.edu.com.edu.ktorapp.model.TaskRepository

class FakeTaskRepository: TaskRepository {
    private val tasks = mutableListOf(
        Task("Task 1", "Description 1", Priority.Low),
        Task("Task 2", "Description 2", Priority.Medium),
        Task("Task 3", "Description 3", Priority.High)
    )


    override suspend fun allTasks(): List<Task> = tasks

    override suspend fun tasksByPriority(priority: Priority) = tasks.filter {
        it.priority == priority
    }

    override suspend fun taskByName(name: String) = tasks.find {
        it.name.equals(name, ignoreCase = true)
    }

    override suspend fun addTask(task: Task) {
        if (taskByName(task.name) != null) {
            throw IllegalStateException("Cannot duplicate with name ${task.name}")
        }
        tasks.add(task)
    }

    override suspend fun removeTask(name: String ): Boolean {
        return tasks.removeIf{ it.name == name }
    }
}