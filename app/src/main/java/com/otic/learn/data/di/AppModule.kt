package com.otic.learn.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.otic.learn.domain.repo.AuthRepository
import com.otic.learn.domain.repo.CoursesRepository
import com.otic.learn.domain.repo.ProgressRepository
import com.otic.learn.data.repo.AuthRepositoryImpl
import com.otic.learn.data.repo.CoursesRepositoryImpl
import com.otic.learn.data.repo.ProgressRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton fun provideAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    @Provides @Singleton fun provideFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Provides @Singleton fun provideAuthRepository(
        auth: FirebaseAuth
    ): AuthRepository = AuthRepositoryImpl(auth)

    @Provides @Singleton fun provideCoursesRepository(
        db: FirebaseFirestore
    ): CoursesRepository = CoursesRepositoryImpl(db)

    @Provides @Singleton fun provideProgressRepository(
        db: FirebaseFirestore,
        auth: FirebaseAuth
    ): ProgressRepository = ProgressRepositoryImpl(db, auth)
}
