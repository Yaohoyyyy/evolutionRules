package com.example.evolutionrules.data

import android.content.Context
import org.json.JSONObject

object PropertyRepository {

    private const val ASSET_FILE = "properties/properties.json"

    fun loadEntries(context: Context): List<PropertyEntry> {
        val text = context.assets.open(ASSET_FILE)
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }
        val root = JSONObject(text)
        val array = root.getJSONArray("entries")
        return buildList {
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                add(
                    PropertyEntry(
                        name = item.getString("name"),
                        theme = item.getString("theme"),
                        description = item.getString("description"),
                    )
                )
            }
        }.sortedBy { it.name }
    }
}
