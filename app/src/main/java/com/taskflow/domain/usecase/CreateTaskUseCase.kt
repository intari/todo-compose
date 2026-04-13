package com.taskflow.domain.usecase

import com.taskflow.domain.model.Task
import com.taskflow.domain.model.TaskPriority
import com.taskflow.domain.repository.TaskRepository
import javax.inject.Inject

class CreateTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        priority: TaskPriority = TaskPriority.MEDIUM,
    ): Long {
        val task = Task(title = title, description = description, priority = priority)
        return repository.insertTask(task)
    }
}
