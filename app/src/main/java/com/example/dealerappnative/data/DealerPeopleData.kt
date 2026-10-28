package com.example.dealerappnative.data

import com.example.dealerappnative.model.*
import org.json.JSONArray
import org.json.JSONObject

/** Fictional staff for the UI prototype; not the private people shown in the browser. */
internal fun sampleDealerPeople(administrator: AdminPersonalProfile): List<DealerPerson> {
    val names = listOf("Анвар Каримов", "Мадина Рахимова", "Азиз Юсупов", "Нодир Алиев", "Дилноза Умарова",
        "Бекзод Расулов", "Сардор Хасанов", "Ислом Набиев", "Жавохир Рустамов")
    return listOf(DealerPerson(currentAdministratorId, administrator, "Администратор")) + names.mapIndexed { i, name ->
        DealerPerson("demo-person-${i + 1}", AdminPersonalProfile(name = name, phone = "+99890000" + (1000 + i)), dealerPersonRoles[i + 1])
    }
}

internal fun encodeDealerPerson(person: DealerPerson): String = JSONObject().put("id", person.id)
    .put("profile", JSONObject(encodeAdminPersonalProfile(person.profile))).put("role", person.role).toString()

internal fun decodeDealerPerson(raw: String): DealerPerson {
    val json = JSONObject(raw)
    return DealerPerson(json.getString("id"), decodeAdminPersonalProfile(json.getJSONObject("profile").toString()), json.getString("role"))
}

internal fun encodeDealerPeople(people: List<DealerPerson>): String = JSONArray().apply {
    people.forEach { put(JSONObject(encodeDealerPerson(it))) }
}.toString()

internal fun decodeDealerPeople(raw: String?, administrator: AdminPersonalProfile): List<DealerPerson> = runCatching {
    val json = JSONArray(raw ?: return sampleDealerPeople(administrator))
    List(json.length()) { decodeDealerPerson(json.getJSONObject(it).toString()) }
        .takeIf { it.count { person -> person.isCurrentAdministrator } == 1 && it.map { person -> person.id }.distinct().size == it.size }
        ?: sampleDealerPeople(administrator)
}.getOrElse { sampleDealerPeople(administrator) }
