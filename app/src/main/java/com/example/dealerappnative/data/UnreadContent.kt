package com.example.dealerappnative.data

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateMapOf

internal class UnreadContent<T : Enum<T>>(
    private val preferences: SharedPreferences,
    newIds: Map<T, Set<String>>
) {
    private val readIds = newIds.keys.associateWith {
        preferences.getStringSet(it.name, emptySet()).orEmpty().toSet()
    }.toMutableMap()
    private val unreadIds = mutableStateMapOf<T, Set<String>>().apply {
        newIds.forEach { (destination, ids) -> put(destination, ids - readIds.getValue(destination)) }
    }

    operator fun get(destination: T): Set<String> = unreadIds[destination].orEmpty()

    fun markRead(destination: T, id: String) {
        val unread = get(destination)
        if (id !in unread) return
        val updated = readIds.getValue(destination) + id
        preferences.edit().putStringSet(destination.name, updated).apply()
        readIds[destination] = updated
        unreadIds[destination] = unread - id
    }
}
