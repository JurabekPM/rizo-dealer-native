package com.example.dealerappnative.location

private val plusCode = Regex("(?i)\\b[23456789CFGHJMPQRVWX]{2,8}\\+[23456789CFGHJMPQRVWX]{2,3}\\b")
private val region = Regex("(?iu)област|\\bобл\\.?|oblast|viloyat|region|республика")
private val country = Regex("(?iu)^(узбекистан|ўзбекистон|uzbekistan|o[‘’'ʻ]?zbekiston|republic of uzbekistan)$")

/** Keep the street, building and town; omit provider codes, region, country and postcodes. */
internal fun compactStoreAddress(raw: String): String = plusCode.replace(raw, "")
    .split(',').map { it.trim() }.filter { part ->
        part.isNotBlank() && !country.matches(part) && !region.containsMatchIn(part) &&
            !Regex("\\d{5,6}").matches(part)
    }.distinct().joinToString(", ")
