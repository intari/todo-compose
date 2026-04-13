package com.taskflow.domain.usecase

import com.taskflow.domain.model.Task
import com.taskflow.domain.repository.TaskRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetTasksUseCaseTest {

    private val repository: TaskRepository = mockk()
    private lateinit var useCase: GetTasksUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetTasksUseCase(repository)
    }

    @Test
    fun `invoke returns flow from repository`() = runTest {
        val tasks = listOf(
            Task(id = 1L, title = "Task 1", description = "Description 1"),
            Task(id = 2L, title = "Task 2", description = "Description 2"),
        )
        every { repository.getTasks() } returns flowOf(tasks)

        val result = useCase().toList().first()

        assertEquals(tasks, result)
        verify(exactly = 1) { repository.getTasks() }
    }

    @Test
    fun `invoke returns empty list when repository has no tasks`() = runTest {
        every { repository.getTasks() } returns flowOf(emptyList())

        val result = useCase().toList().first()

        assertEquals(emptyList<Task>(), result)
    }

    @Test
    fun `invoke delegates directly to repository without transformation`() = runTest {
        val expected = listOf(Task(id = 99L, title = "T", description = "D"))
        every { repository.getTasks() } returns flowOf(expected)

        assertEquals(expected, useCase().toList().first())
        verify(exactly = 1) { repository.getTasks() }
    }
}
