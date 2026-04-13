package com.taskflow.presentation.taskdetail

import com.taskflow.presentation.base.UiEvent

sealed interface TaskDetailEvent : UiEvent {
    data class LoadTask(val taskId: Long) : TaskDetailEvent
    data object EditClicked : TaskDetailEvent
    data object DeleteClicked : TaskDetailEvent
    data object BackClicked : TaskDetailEvent
}
