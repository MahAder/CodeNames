package com.ader.codenames.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ader.codenames.data.model.TeamDBModel

@Dao
interface TeamDao {
    @Insert
    suspend fun insert(teamDBModel: TeamDBModel)
}