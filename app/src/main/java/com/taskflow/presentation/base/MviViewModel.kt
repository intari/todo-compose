package com.taskflow.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Базовый ViewModel для MVI-архитектуры.
 *
 * @param S – неизменяемое состояние экрана (UiState)
 * @param E – намерения пользователя (UiEvent)
 * @param F – одноразовые побочные эффекты (UiEffect): навигация, Snackbar и т.п.
 */
abstract class MviViewModel<S : UiState, E : UiEvent, F : UiEffect>(
    initialState: S,
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    // Channel гарантирует, что эффект не потеряется при повороте экрана
    private val _effects = Channel<F>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    protected val currentState: S get() = _state.value

    abstract fun onEvent(event: E)

    protected fun setState(reducer: S.() -> S) {
        _state.value = currentState.reducer()
    }

    protected fun emitEffect(effect: F) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
