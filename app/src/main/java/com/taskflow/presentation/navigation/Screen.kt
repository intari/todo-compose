package com.taskflow.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object TaskListRoute

@Serializable
data class TaskDetailRoute(val taskId: Long)

@Serializable
data class AddEditTaskRoute(val taskId: Long = -1L)

@Serializable
object SettingsRoute
