package com.taskflow.presentation.tasklist

import com.taskflow.presentation.base.UiEffect

sealed interface TaskListEffect : UiEffect {
    data class NavigateToDetail(val taskId: Long) : TaskListEffect
    data object NavigateToAddTask : TaskListEffect
    data object NavigateToSettings : TaskListEffect
    data class ShowSnackbar(val message: String) : TaskListEffect
    data class ShowError(val message: String) : TaskListEffect
}
