    # CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & test commands

```bash
# Assemble debug APK
./gradlew assembleDebug

# Run all unit tests (JUnit 5 via useJUnitPlatform())
./gradlew test

# Run tests for a single class
./gradlew test --tests "com.taskflow.presentation.tasklist.TaskListViewModelTest"

# Run lint
./gradlew lint

# Check compilation without assembling
./gradlew compileDebugKotlin

# Full clean build
./gradlew clean assembleDebug
```

> On Windows use `gradlew.bat` instead of `./gradlew`.  
> `gradle/wrapper/gradle-wrapper.jar` must be present (not tracked by git) — run `gradle wrapper` in Android Studio to generate it.

## Architecture overview

**Single-module Clean Architecture + MVI**, package root `com.taskflow`:

```
data/       ← Room (TaskEntity, TaskDao, TaskDatabase), mapper, TaskRepositoryImpl
domain/     ← Task model, TaskRepository interface, 4 use-cases (Get/Create/Update/Delete)
di/         ← Hilt modules: DatabaseModule (Room + DAO), RepositoryModule (binds impl to interface)
presentation/
  base/     ← MviViewModel<S,E,F>, UiState, UiEvent, UiEffect marker interfaces
  navigation/ ← type-safe @Serializable routes + NavHost (AppNavGraph)
  tasklist/   ← TaskListState/Event/Effect/ViewModel/Screen
  taskdetail/ ← TaskDetailState/Event/Effect/ViewModel/Screen
ui/theme/   ← Material 3 TaskFlowTheme (Dynamic Color on API 31+), Color, Type
```

### MVI contract

Every screen owns three files that must stay in sync:

| File | Purpose |
|---|---|
| `XxxState : UiState` | Immutable snapshot — what the UI renders |
| `XxxEvent : UiEvent` | User intentions (sealed interface) — sent via `viewModel.onEvent()` |
| `XxxEffect : UiEffect` | One-shot side-effects (sealed interface) — navigation, Snackbar |

`MviViewModel` exposes:
- `state: StateFlow<S>` — collected with `collectAsStateWithLifecycle()`
- `effects: Flow<F>` — collected in `LaunchedEffect(Unit)` inside the composable

`setState { copy(...) }` is the only way to mutate state; `emitEffect(...)` sends an effect through a `Channel.BUFFERED`.

### Navigation

Routes are `@Serializable` objects/data classes in `Screen.kt`. The NavHost uses `composable<RouteType>` and `backStackEntry.toRoute<RouteType>()`. `taskId == -1L` signals "create new task" mode in `TaskDetailScreen`.

### Dependency injection

Hilt is wired at `Application` level (`@HiltAndroidApp TaskFlowApp`). All ViewModels use `@HiltViewModel`. Compose entry-points use `hiltViewModel()` from `hilt-navigation-compose`.

### Testing conventions

- Tests are local JUnit 5 (`src/test/`), no instrumented tests yet.
- `StandardTestDispatcher` + `Dispatchers.setMain` in `@BeforeEach` / `Dispatchers.resetMain` in `@AfterEach`.
- Use `app.cash.turbine` `.test { }` blocks for Flow/StateFlow/Effect assertions.
- MockK for mocking; `coEvery` for suspend functions, `every` for regular ones.
