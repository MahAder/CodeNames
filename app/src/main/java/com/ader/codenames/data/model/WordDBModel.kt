package com.ader.codenames.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ader.codenames.data.Constants

@Entity(tableName = "word")
data class WordDBModel (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Constants.WORD_ID)
    val id: Int = 0,

    @ColumnInfo(name = "uaValue")
    val uaValue: String
)