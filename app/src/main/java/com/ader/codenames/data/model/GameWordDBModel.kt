package com.ader.codenames.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.ader.codenames.data.Constants

@Entity(
    tableName = "active_word",
    primaryKeys = [Constants.GAME_ID, Constants.WORD_ID],
    foreignKeys = [
        ForeignKey(
            entity = GameDBModel::class,
            onDelete = CASCADE,
            onUpdate = CASCADE,
            parentColumns = [Constants.GAME_ID],
            childColumns = [Constants.GAME_ID]
        ),

        ForeignKey(
            entity = WordDBModel::class,
            onDelete = CASCADE,
            onUpdate = CASCADE,
            parentColumns = [Constants.WORD_ID],
            childColumns = [Constants.WORD_ID]
        )
    ]
)
data class GameWordDBModel(
    @ColumnInfo(name = Constants.WORD_ID)
    val wordId: Int,

    @ColumnInfo(name = Constants.GAME_ID)
    val gameId: Int,

    @ColumnInfo(name = "type")
    val type: Int,

    @ColumnInfo(name = "index")
    val index: Int,

    @ColumnInfo(name = "is_open")
    val isOpen: Boolean = false,

    @ColumnInfo(name = "value")
    val value: String
)