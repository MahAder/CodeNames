package com.ader.codenames.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ader.codenames.data.Constants

@Entity(
    tableName = "team",
    foreignKeys = [
        ForeignKey(
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            entity = GameDBModel::class,
            parentColumns = [Constants.GAME_ID],
            childColumns = [Constants.GAME_ID]
        )
    ]
)
data class TeamDBModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Constants.TEAM_ID)
    val id: Int = 0,

    @ColumnInfo(name = Constants.GAME_ID)
    val gameId: Int,

    @ColumnInfo(name = "order")
    val orderNumber: Int,

    @ColumnInfo(name = "team_color")
    val teamColor: Int
)