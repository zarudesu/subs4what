package com.subs4what.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.subs4what.app.firebase.MemberData
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "subs4what_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        private val KEY_MEMBER_NUMBER = intPreferencesKey("member_number")
        private val KEY_CREATED_AT = stringPreferencesKey("created_at")
        private val KEY_ONBOARDED = booleanPreferencesKey("onboarded")
    }

    suspend fun saveMemberData(data: MemberData) {
        context.dataStore.edit { prefs ->
            prefs[KEY_MEMBER_NUMBER] = data.memberNumber
            prefs[KEY_CREATED_AT] = data.createdAt
        }
    }

    suspend fun getMemberData(): MemberData? {
        val prefs = context.dataStore.data.first()
        val number = prefs[KEY_MEMBER_NUMBER] ?: return null
        val createdAt = prefs[KEY_CREATED_AT] ?: return null
        return MemberData(memberNumber = number, createdAt = createdAt)
    }

    suspend fun isOnboarded(): Boolean {
        val prefs = context.dataStore.data.first()
        return prefs[KEY_ONBOARDED] == true
    }

    suspend fun setOnboarded() {
        context.dataStore.edit { prefs ->
            prefs[KEY_ONBOARDED] = true
        }
    }
}
