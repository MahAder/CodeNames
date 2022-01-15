package com.ader.codenames.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ader.codenames.data.dao.GameDao
import com.ader.codenames.data.dao.GameWordDao
import com.ader.codenames.data.dao.TeamDao
import com.ader.codenames.data.dao.WordDao
import com.ader.codenames.data.model.*

@Database(
    entities = [
        WordDBModel::class,
        GameWordDBModel::class,
        GameDBModel::class,
        TeamDBModel::class
    ],
    version = Constants.DATABASE_VERSION
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    abstract fun gameDao(): GameDao

    abstract fun teamDao(): TeamDao

    abstract fun gameWordDao(): GameWordDao
}