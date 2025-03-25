package com.dna.beyoureyes.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// AppDataStore.kt
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token_data")