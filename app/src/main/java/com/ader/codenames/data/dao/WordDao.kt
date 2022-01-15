package com.ader.codenames.data.dao

import androidx.room.*
import com.ader.codenames.data.model.WordDBModel

@Dao
interface WordDao {
    @Insert
    suspend fun insert(wordDBModel: WordDBModel): Long

    @Query("SELECT * FROM word ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomWords(count: Int): List<WordDBModel>

    @Query("SELECT * FROM word ORDER BY RANDOM() LIMIT 1")
    suspend fun getOneRandomWord(): WordDBModel

    @Update
    suspend fun updateWord(wordDBModel: WordDBModel)

    @Delete
    suspend fun deleteWord(wordDBModel: WordDBModel)
}