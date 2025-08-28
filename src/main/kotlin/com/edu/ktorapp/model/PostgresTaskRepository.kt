package com.edu.com.edu.ktorapp.model

import com.edu.com.edu.ktorapp.db.TaskDAO
import com.edu.com.edu.ktorapp.db.TaskTable
import com.edu.com.edu.ktorapp.db.daoToModel
import com.edu.com.edu.ktorapp.db.suspendTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

class PostgresTaskRepository: TaskRepository {

    override suspend fun allTasks(): List<Task> {
        return suspendTransaction {
             TaskDAO.all().map(::daoToModel)
        }
    }

    override suspend fun tasksByPriority(priority: Priority): List<Task> {
        return suspendTransaction {
            TaskDAO.find{ (TaskTable.priority eq priority.toString()) }
                .map(::daoToModel)
        }
    }

    override suspend fun taskByName(name: String): Task? {
        return suspendTransaction {
            TaskDAO.find{ (TaskTable.name eq name) }
                .limit(1)
                .map(::daoToModel)
                .firstOrNull()
        }
    }

    override suspend fun addTask(task: Task): Unit {
        suspendTransaction {
            TaskDAO.new {
                name = task.name
                description = task.description
                priority = task.priority.toString()
            }
        }
    }

    override suspend fun removeTask(name: String): Boolean {
        return suspendTransaction {
            val rowsDeleted = TaskTable.deleteWhere {
                TaskTable.name eq name
            }
            rowsDeleted == 1
        }
    }
}