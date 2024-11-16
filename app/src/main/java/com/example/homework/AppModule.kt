package com.example.homework

import android.content.Context
import androidx.room.Room
import com.example.homework.db.ActionsDao
import com.example.homework.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "db"
        )
            .addMigrations(AppDatabase.Migration23_24)
            .build()
    }

    @Provides
    fun provideActionsDao(database: AppDatabase):ActionsDao {
        return database.actionsDao()
    }
}