package com.taskflow.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.taskflow.presentation.addedit.AddEditTaskScreen
import com.taskflow.presentation.settings.SettingsScreen
import com.taskflow.presentation.taskdetail.TaskDetailScreen
import com.taskflow.presentation.tasklist.TaskListScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TaskListRoute,
    ) {
        // Список задач
        composable<TaskListRoute> {
            TaskListScreen(
                onNavigateToDetail = { taskId -> navController.navigate(TaskDetailRoute(taskId)) },
                onNavigateToAdd = { navController.navigate(AddEditTaskRoute()) },
                onNavigateToSettings = { navController.navigate(SettingsRoute) },
            )
        }

        // Просмотр задачи
        composable<TaskDetailRoute> { back ->
            val route: TaskDetailRoute = back.toRoute()
            TaskDetailScreen(
                taskId = route.taskId,
                onBack = { navController.popBackStack() },
                onNavigateToEdit = { taskId -> navController.navigate(AddEditTaskRoute(taskId)) },
            )
        }

        // Создание / редактирование задачи
        composable<AddEditTaskRoute> { back ->
            val route: AddEditTaskRoute = back.toRoute()
            AddEditTaskScreen(
                taskId = route.taskId,
                onBack = { navController.popBackStack() },
            )
        }

        // Настройки
        composable<SettingsRoute> {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
