package com.taskflow.presentation.taskdetail

import androidx.lifecycle.viewModelScope
import com.taskflow.domain.usecase.DeleteTaskUseCase
import com.taskflow.domain.usecase.GetTasksUseCase
import com.taskflow.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
) : MviViewModel<TaskDetailState, TaskDetailEvent, TaskDetailEffect>(TaskDetailState()) {

    override fun onEvent(event: TaskDetailEvent) {
        when (event) {
            is TaskDetailEvent.LoadTask -> loadTask(event.taskId)
            TaskDetailEvent.EditClicked -> currentState.task?.let {
                emitEffect(
                    TaskDetailEffect.NavigateToEdit(
                        it.id
                    )
                )
            }

            TaskDetailEvent.DeleteClicked -> delete()
            TaskDetailEvent.BackClicked -> emitEffect(TaskDetailEffect.NavigateBack)
        }
    }

    private fun loadTask(taskId: Long) {
        setState { copy(isLoading = true) }
        getTasksUseCase()
            .onEach { tasks ->
                setState { copy(task = tasks.find { it.id == taskId }, isLoading = false) }
            }
            .catch { e -> setState { copy(isLoading = false, error = e.message) } }
            .launchIn(viewModelScope)
    }

    private fun delete() {
        val taskId = currentState.task?.id ?: return
        viewModelScope.launch {
            runCatching { deleteTaskUseCase(taskId) }
                .onSuccess { emitEffect(TaskDetailEffect.NavigateBack) }
                .onFailure {
                    emitEffect(
                        TaskDetailEffect.ShowError(
                            it.message ?: "Ошибка удаления"
                        )
                    )
                }
        }
    }
}
