package com.taskflow.presentation.tasklist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taskflow.domain.model.Task
import com.taskflow.domain.model.TaskFilter
import com.taskflow.domain.model.TaskPriority

private val filters = listOf(TaskFilter.ALL, TaskFilter.ACTIVE, TaskFilter.COMPLETED)

private fun TaskFilter.label() = when (this) {
    TaskFilter.ALL -> "Все"
    TaskFilter.ACTIVE -> "Активные"
    TaskFilter.COMPLETED -> "Завершённые"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: TaskListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TaskListEffect.NavigateToDetail -> onNavigateToDetail(effect.taskId)
                TaskListEffect.NavigateToAddTask -> onNavigateToAdd()
                TaskListEffect.NavigateToSettings -> onNavigateToSettings()
                is TaskListEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                is TaskListEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TaskFlow") },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(TaskListEvent.SettingsClicked) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Настройки")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onEvent(TaskListEvent.AddTaskClicked) }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить задачу")
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Фильтр-табы
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(state.activeFilter),
                edgePadding = 0.dp,
            ) {
                filters.forEach { filter ->
                    Tab(
                        selected = state.activeFilter == filter,
                        onClick = { viewModel.onEvent(TaskListEvent.FilterChanged(filter)) },
                        text = { Text(filter.label()) },
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                    state.error != null -> Text(
                        text = state.error.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                    )

                    state.tasks.isEmpty() -> Text(
                        text = "Задач нет",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center),
                    )

                    else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.tasks, key = { it.id }) { task ->
                            TaskItem(
                                task = task,
                                onTaskClick = { viewModel.onEvent(TaskListEvent.TaskClicked(task.id)) },
                                onCheckedChange = { checked ->
                                    viewModel.onEvent(
                                        TaskListEvent.ToggleTaskCompleted(
                                            task.id,
                                            checked
                                        )
                                    )
                                },
                                onDelete = { viewModel.onEvent(TaskListEvent.DeleteTask(task.id)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onTaskClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = task.isCompleted, onCheckedChange = onCheckedChange)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            )
            if (task.description.isNotBlank()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Бейдж приоритета
        PriorityBadge(priority = task.priority)

        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Удалить",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun PriorityBadge(priority: TaskPriority) {
    val color = when (priority) {
        TaskPriority.HIGH -> MaterialTheme.colorScheme.error
        TaskPriority.MEDIUM -> MaterialTheme.colorScheme.primary
        TaskPriority.LOW -> MaterialTheme.colorScheme.outline
    }
    Badge(containerColor = color) {
        Text(
            text = when (priority) {
                TaskPriority.HIGH -> "H"
                TaskPriority.MEDIUM -> "M"
                TaskPriority.LOW -> "L"
            },
        )
    }
}
