package com.taskflow.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Зарезервирован для провайдинга зависимостей уровня приложения
// (например: CoroutineDispatchers, AnalyticsService, RemoteConfig и т.д.)
// TODO: прицепить Яндекс.Метрику
// TODO: прицепить схему с IoDispatcher/MainDispatcher
@Module
@InstallIn(SingletonComponent::class)
object AppModule
