package com.example.homework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Actions::class, Collections::class, Actors::class, ActorInMovies::class, Movies::class, WorkerInMovies::class, MovieWorkers::class, RelatedMovies::class, Images::class, Serials::class, MovieCount::class], version = 24)
abstract class AppDatabase : RoomDatabase() {
    abstract fun actionsDao(): ActionsDao

   object Migration23_24 : Migration(23,24) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE testS(id INTEGER NOT NULL PRIMARY KEY, relatedMoviesCount INTEGER NOT NULL, imagesCount INTEGER NOT NULL, actorsCount INTEGER NOT NULL, workersCount INTEGER NOT NULL)")
            db.execSQL("DROP TABLE MovieCount")
            db.execSQL("ALTER TABLE testS RENAME TO MovieCount")
        }
    }
}