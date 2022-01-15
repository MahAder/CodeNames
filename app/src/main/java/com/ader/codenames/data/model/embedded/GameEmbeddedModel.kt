package com.ader.codenames.data.model.embedded

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.ader.codenames.data.Constants
import com.ader.codenames.data.model.*

data class GameEmbeddedModel(
    @Embedded
    val gameDBModel: GameDBModel,

    @Relation(
        parentColumn = Constants.GAME_ID,
        entityColumn = Constants.WORD_ID,
        associateBy = Junction(GameWordDBModel::class)
    )
    val gameWords: List<GameWordDBModel>,

    @Relation(
        parentColumn = Constants.GAME_ID,
        entityColumn = Constants.GAME_ID
    )
    val teams: List<TeamDBModel>
)