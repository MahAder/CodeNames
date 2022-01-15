package com.ader.codenames.domain.utils

object GameConstants {
    const val DEFAULT_GAME_ID = 0
    const val DEFAULT_WORDS_COUNT = 25
    const val DEFAULT_FIRST_TEAM_WORDS_COUNT = 9
    const val DEFAULT_SECOND_TEAM_WORDS_COUNT = 8
    const val DEFAULT_KILLER_WORDS_COUNT = 1

    enum class Team(val value: Int) {
        FIRST_TEAM(0),
        SECOND_TEAM(1);
    }

    enum class TeamColor(val value: Int) {
        RED(0),
        BLUE(1);
    }

    enum class GameWordType(val value: Int) {
        FIRST_TEAM(0),
        SECOND_TEAM(1),
        NEUTRAL(2),
        KILLER(3);
    }
}