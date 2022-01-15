package com.ader.codenames.data.dao

import androidx.room.*
import com.ader.codenames.data.model.GameWordDBModel

@Dao
interface GameWordDao {
    @Insert
    suspend fun insert(gameWordDBModel: GameWordDBModel)

    @Update
    suspend fun update(gameWordDBModel: GameWordDBModel)

    @Delete
    suspend fun delete(gameWordDBModel: GameWordDBModel)
}