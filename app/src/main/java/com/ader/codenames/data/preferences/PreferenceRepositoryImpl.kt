package com.ader.codenames.data.preferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.ader.codenames.data.Constants
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(private val context: Context): PreferenceRepository {
    private val prefs = context.getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE)

    override fun isWordsMigratedToDB(): Boolean {
        return prefs.getBoolean(Constants.IS_WORDS_MIGRATED_TO_DB_KEY, false)
    }

    override fun saveWordsMigratedToDB() {
        prefs.edit().putBoolean(Constants.IS_WORDS_MIGRATED_TO_DB_KEY, true).apply()
    }
}