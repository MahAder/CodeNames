package com.ader.codenames.presentation.model

import com.ader.codenames.domain.model.WordModel

data class WordUIModel(
    val word: String,
    val type: WordType
) {

    enum class WordType {
        CLOSED, RED, BLUE, YELLOW, BLACK
    }
}