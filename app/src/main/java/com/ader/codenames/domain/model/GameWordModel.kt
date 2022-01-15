package com.ader.codenames.domain.model

import com.ader.codenames.domain.utils.GameConstants

data class GameWordModel(
    val word: WordModel,
    val type: GameConstants.GameWordType,
    var isOpen: Boolean,
    val index: Int
)