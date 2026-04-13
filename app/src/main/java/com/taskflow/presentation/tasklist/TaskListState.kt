package com.taskflow.presentation.tasklist

import com.taskflow.domain.model.Task
import com.taskflow.domain.model.TaskFilter
import com.taskflow.presentation.base.UiState

data class TaskListState(
    val tasks: List<Task> = emptyList(),
    val activeFilter: TaskFilter = TaskFilter.ALL,
    val isLoading: Boolean = false,
    val error: String? = null,
) : UiState
