package com.taskflow.presentation.taskdetail

import com.taskflow.presentation.base.UiEffect

sealed interface TaskDetailEffect : UiEffect {
    data object NavigateBack : TaskDetailEffect
    data class NavigateToEdit(val taskId: Long) : TaskDetailEffect
    data class ShowError(val message: String) : TaskDetailEffect
}
