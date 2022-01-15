package com.ader.codenames.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ader.codenames.data.Constants

@Entity(tableName = "game")
data class GameDBModel(
    @PrimaryKey
    @ColumnInfo(name = Constants.GAME_ID)
    val id: Int,

    @ColumnInfo(name = "move_number")
    val moveNumber: Int,

    @ColumnInfo(name = "move_team")
    val moveTeam: Int,

    @ColumnInfo(name = "master_key")
    val masterKey: String
)