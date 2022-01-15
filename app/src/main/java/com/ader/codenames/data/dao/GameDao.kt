package com.ader.codenames.data.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.ader.codenames.data.model.GameDBModel
import com.ader.codenames.data.model.embedded.GameEmbeddedModel

@Dao
interface GameDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(gameDBModel: GameDBModel)

    @Update
    suspend fun update(gameDBModel: GameDBModel)

    @Delete
    suspend fun delete(gameDBModel: GameDBModel)

    @Transaction
    @Query("SELECT * FROM game LIMIT 1")
    suspend fun getActiveGame(): GameEmbeddedModel?
}