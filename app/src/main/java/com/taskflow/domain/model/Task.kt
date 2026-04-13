package com.taskflow.domain.model

data class Task(
    val id: Long = 0L,
    val title: String,
    val description: String,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
