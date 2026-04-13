package com.taskflow.data.mapper

import com.taskflow.data.local.TaskEntity
import com.taskflow.domain.model.Task
import com.taskflow.domain.model.TaskPriority

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    priority = TaskPriority.valueOf(priority),
    isCompleted = isCompleted,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    priority = priority.name,
    isCompleted = isCompleted,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
