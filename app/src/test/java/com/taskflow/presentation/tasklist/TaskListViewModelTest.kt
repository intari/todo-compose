package com.taskflow.presentation.tasklist

import app.cash.turbine.test
import com.taskflow.domain.model.Task
import com.taskflow.domain.model.TaskFilter
import com.taskflow.domain.usecase.DeleteTaskUseCase
import com.taskflow.domain.usecase.GetFilteredTasksUseCase
import com.taskflow.domain.usecase.UpdateTaskUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getFilteredTasksUseCase: GetFilteredTasksUseCase = mockk()
    private val updateTaskUseCase: UpdateTaskUseCase = mockk()
    private val deleteTaskUseCase: DeleteTaskUseCase = mockk()

    private lateinit var viewModel: TaskListViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getFilteredTasksUseCase(any()) } returns flowOf(emptyList())
        viewModel = TaskListViewModel(getFilteredTasksUseCase, updateTaskUseCase, deleteTaskUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state contains empty task list`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(emptyList<Task>(), state.tasks)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `tasks are loaded and propagated to state`() = runTest {
        val tasks = listOf(
            Task(id = 1L, title = "Task 1", description = "Desc 1"),
            Task(id = 2L, title = "Task 2", description = "Desc 2"),
        )
        every { getFilteredTasksUseCase(any()) } returns flowOf(tasks)

        viewModel = TaskListViewModel(getFilteredTasksUseCase, updateTaskUseCase, deleteTaskUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(tasks, state.tasks)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `FilterChanged switches active filter`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(TaskListEvent.FilterChanged(TaskFilter.ACTIVE))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertEquals(TaskFilter.ACTIVE, awaitItem().activeFilter)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `TaskClicked emits NavigateToDetail effect`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onEvent(TaskListEvent.TaskClicked(taskId = 42L))
            assertEquals(TaskListEffect.NavigateToDetail(42L), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `AddTaskClicked emits NavigateToAddTask effect`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onEvent(TaskListEvent.AddTaskClicked)
            assertEquals(TaskListEffect.NavigateToAddTask, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SettingsClicked emits NavigateToSettings effect`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onEvent(TaskListEvent.SettingsClicked)
            assertEquals(TaskListEffect.NavigateToSettings, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `DeleteTask calls use case and emits ShowSnackbar`() = runTest {
        coEvery { deleteTaskUseCase(any()) } returns Unit
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onEvent(TaskListEvent.DeleteTask(taskId = 1L))
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(TaskListEffect.ShowSnackbar("Задача удалена"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
