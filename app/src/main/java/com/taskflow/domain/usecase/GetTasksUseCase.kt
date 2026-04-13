package com.taskflow.domain.usecase

import com.taskflow.domain.model.Task
import com.taskflow.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    operator fun invoke(): Flow<List<Task>> = repository.getTasks()
}
