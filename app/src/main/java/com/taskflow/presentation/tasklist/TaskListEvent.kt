package com.taskflow.presentation.tasklist

import com.taskflow.domain.model.TaskFilter
import com.taskflow.presentation.base.UiEvent

sealed interface TaskListEvent : UiEvent {
    data class FilterChanged(val filter: TaskFilter) : TaskListEvent
    data class TaskClicked(val taskId: Long) : TaskListEvent
    data class ToggleTaskCompleted(val taskId: Long, val isCompleted: Boolean) : TaskListEvent
    data class DeleteTask(val taskId: Long) : TaskListEvent
    data object AddTaskClicked : TaskListEvent
    data object SettingsClicked : TaskListEvent
}
