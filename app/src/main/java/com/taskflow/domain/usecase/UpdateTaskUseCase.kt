package com.taskflow.domain.usecase

import com.taskflow.domain.model.Task
import com.taskflow.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    suspend operator fun invoke(task: Task) {
        repository.updateTask(task.copy(updatedAt = System.currentTimeMillis()))
    }
}
