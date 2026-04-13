package com.taskflow.domain.usecase

import com.taskflow.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    suspend operator fun invoke(id: Long) = repository.deleteTaskById(id)
}
