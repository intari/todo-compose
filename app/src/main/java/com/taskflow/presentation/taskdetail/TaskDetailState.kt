package com.taskflow.presentation.taskdetail

import com.taskflow.domain.model.Task
import com.taskflow.presentation.base.UiState

data class TaskDetailState(
    val task: Task? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
) : UiState
