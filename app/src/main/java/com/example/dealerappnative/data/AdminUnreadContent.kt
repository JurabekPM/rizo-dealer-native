package com.example.dealerappnative.data

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateMapOf
import com.example.dealerappnative.navigation.DealerDestination

internal class AdminUnreadContent(
    private val preferences: SharedPreferences,
    newIds: Map<DealerDestination, Set<String>>
) {
    private val readIds = DealerDestination.entries.associateWith {
        preferences.getStringSet(it.name, emptySet()).orEmpty().toSet()
    }.toMutableMap()
    private val unreadIds = mutableStateMapOf<DealerDestination, Set<String>>().apply {
        newIds.forEach { (destination, ids) -> put(destination, ids - readIds.getValue(destination)) }
    }

    operator fun get(destination: DealerDestination): Set<String> = unreadIds[destination].orEmpty()

    fun markRead(destination: DealerDestination, id: String) {
        val unread = get(destination)
        if (id !in unread) return
        val updated = readIds.getValue(destination) + id
        preferences.edit().putStringSet(destination.name, updated).apply()
        readIds[destination] = updated
        unreadIds[destination] = unread - id
    }
}
