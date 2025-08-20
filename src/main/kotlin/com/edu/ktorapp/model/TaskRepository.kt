package com.edu.com.edu.ktorapp.model

object TaskRepository {
    private val tasks = mutableListOf(
        Task("Task 1", "Description 1", Priority.Low),
        Task("Task 2", "Description 2", Priority.Medium),
        Task("Task 3", "Description 3", Priority.High)
    )


    fun allTasks(): List<Task> = tasks

    fun tasksByPriority(priority: Priority) = tasks.filter {
        it.priority == priority
    }

    fun taskByName(name: String) = tasks.find {
        it.name.equals(name, ignoreCase = true)
    }

    fun addTask(task: Task) {
        if (taskByName(task.name) != null) {
            throw IllegalStateException("Cannot duplicate with name ${task.name}")
        }
        tasks.add(task)
    }

    fun removeTask(name: String ): Boolean {
        return tasks.removeIf{ it.name == name }
    }
}

