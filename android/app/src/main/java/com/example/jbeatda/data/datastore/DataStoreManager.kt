package com.example.jbeatda.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val EXAMPLE_KEY = stringPreferencesKey("example_key")
    }

    val exampleFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[EXAMPLE_KEY] ?: "default"
        }

    suspend fun saveExample(value: String) {
        context.dataStore.edit { preferences ->
            preferences[EXAMPLE_KEY] = value
        }
    }
}