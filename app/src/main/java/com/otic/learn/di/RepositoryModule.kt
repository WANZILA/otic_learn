package com.otic.learn.di

import com.otic.learn.data.repository.AuthRepositoryImpl
import com.otic.learn.data.repository.CourseRepositoryImpl
import com.otic.learn.data.repository.NotesRepositoryImpl
import com.otic.learn.domain.repository.AuthRepository
import com.otic.learn.domain.repository.CourseRepository
import com.otic.learn.domain.repository.NotesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCourseRepository(
        impl: CourseRepositoryImpl
    ): CourseRepository


    @Binds
    @Singleton
    abstract fun bindNotesRepository(
        impl: NotesRepositoryImpl
    ): NotesRepository
    // Later we'll bind:
    // - CourseRepositoryImpl
    // - NotesRepositoryImpl
    // - MessagesRepositoryImpl
    // - etc.
}
