package com.example.dealerappnative.model

import androidx.annotation.DrawableRes

internal data class AgentHomeMetric(
    val title: String,
    val value: String,
    @param:DrawableRes val icon: Int,
    val detail: String? = null,
    val progress: Float? = null,
    val negative: Boolean = false
)
