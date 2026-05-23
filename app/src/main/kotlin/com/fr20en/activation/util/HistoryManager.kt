package com.fr20en.activation.util

import android.content.Context
import android.content.SharedPreferences
import com.fr20en.activation.model.ActivationCode
import org.json.JSONArray
import org.json.JSONObject

object HistoryManager {
    private const val PREF_NAME = "activation_history"
    private const val KEY_RECORDS = "records"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun load(context: Context): MutableList<ActivationCode> {
        val json = prefs(context).getString(KEY_RECORDS, "[]") ?: "[]"
        val arr = JSONArray(json)
        val list = mutableListOf<ActivationCode>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(
                ActivationCode(
                    id = obj.getLong("id"),
                    appName = obj.getString("appName"),
                    code = obj.getString("code"),
                    machineCode = obj.getString("machineCode"),
                    durationMs = obj.getLong("durationMs"),
                    createdAt = obj.getLong("createdAt"),
                )
            )
        }
        return list
    }

    fun save(context: Context, list: List<ActivationCode>) {
        val trimmed = if (list.size > 100) list.takeLast(100) else list
        val arr = JSONArray()
        trimmed.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("appName", item.appName)
                put("code", item.code)
                put("machineCode", item.machineCode)
                put("durationMs", item.durationMs)
                put("createdAt", item.createdAt)
            }
            arr.put(obj)
        }
        prefs(context).edit().putString(KEY_RECORDS, arr.toString()).apply()
    }

    fun add(context: Context, code: ActivationCode) {
        val list = load(context)
        list.add(code)
        save(context, list)
    }

    fun remove(context: Context, id: Long) {
        val list = load(context)
        list.removeAll { it.id == id }
        save(context, list)
    }

    fun clear(context: Context) {
        prefs(context).edit().remove(KEY_RECORDS).apply()
    }

    fun formatDuration(ms: Long): String {
        return when {
            ms < 60000 -> "${ms / 1000}秒"
            ms < 3600000 -> "${ms / 60000}分钟"
            ms < 86400000 -> "${ms / 3600000}小时"
            ms < 2592000000 -> "${ms / 86400000}天"
            ms < 31536000000 -> "${ms / 2592000000}个月"
            else -> "${ms / 31536000000}年"
        }
    }
}
