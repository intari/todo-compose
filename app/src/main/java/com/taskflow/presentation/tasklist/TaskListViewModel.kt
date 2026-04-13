package com.taskflow.presentation.tasklist

import androidx.lifecycle.viewModelScope
import com.taskflow.domain.model.TaskFilter
import com.taskflow.domain.usecase.DeleteTaskUseCase
import com.taskflow.domain.usecase.GetFilteredTasksUseCase
import com.taskflow.domain.usecase.UpdateTaskUseCase
import com.taskflow.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getFilteredTasksUseCase: GetFilteredTasksUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
) : MviViewModel<TaskListState, TaskListEvent, TaskListEffect>(TaskListState()) {

    init {
        observeTasks()
    }

    override fun onEvent(event: TaskListEvent) {
        when (event) {
            is TaskListEvent.FilterChanged -> onFilterChanged(event.filter)
            is TaskListEvent.TaskClicked -> emitEffect(TaskListEffect.NavigateToDetail(event.taskId))
            TaskListEvent.AddTaskClicked -> emitEffect(TaskListEffect.NavigateToAddTask)
            TaskListEvent.SettingsClicked -> emitEffect(TaskListEffect.NavigateToSettings)
            is TaskListEvent.ToggleTaskCompleted -> toggleCompletion(
                event.taskId,
                event.isCompleted
            )

            is TaskListEvent.DeleteTask -> deleteTask(event.taskId)
        }
    }

    private fun observeTasks() {
        // При смене фильтра flatMapLatest автоматически переподписывается
        state
            .flatMapLatest { s -> getFilteredTasksUseCase(s.activeFilter) }
            .onEach { tasks -> setState { copy(tasks = tasks, isLoading = false, error = null) } }
            .catch { e -> setState { copy(isLoading = false, error = e.message) } }
            .launchIn(viewModelScope)
    }

    private fun onFilterChanged(filter: TaskFilter) {
        if (currentState.activeFilter == filter) return
        setState { copy(activeFilter = filter, isLoading = true) }
    }

    private fun toggleCompletion(taskId: Long, isCompleted: Boolean) {
        val task = currentState.tasks.find { it.id == taskId } ?: return
        viewModelScope.launch {
            runCatching { updateTaskUseCase(task.copy(isCompleted = isCompleted)) }
                .onFailure {
                    emitEffect(
                        TaskListEffect.ShowError(
                            it.message ?: "Ошибка обновления"
                        )
                    )
                }
        }
    }

    private fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            runCatching { deleteTaskUseCase(taskId) }
                .onSuccess { emitEffect(TaskListEffect.ShowSnackbar("Задача удалена")) }
                .onFailure { emitEffect(TaskListEffect.ShowError(it.message ?: "Ошибка удаления")) }
        }
    }
}
