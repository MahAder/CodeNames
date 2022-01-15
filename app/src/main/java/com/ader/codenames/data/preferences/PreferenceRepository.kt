package com.ader.codenames.data.preferences

interface PreferenceRepository {
    fun isWordsMigratedToDB(): Boolean

    fun saveWordsMigratedToDB()
}