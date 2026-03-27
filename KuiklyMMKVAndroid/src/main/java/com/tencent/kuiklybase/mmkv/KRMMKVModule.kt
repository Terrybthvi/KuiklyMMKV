package com.tencent.kuiklybase.mmkv

import com.tencent.kuikly.core.render.android.export.KuiklyRenderBaseModule
import com.tencent.kuikly.core.render.android.export.KuiklyRenderCallback
import com.tencent.mmkv.MMKV
import org.json.JSONArray
import org.json.JSONObject

/**
 * Android 原生 MMKV Module
 *
 * 接收 KMP 层 MMKVModule 的调用，委派到 MMKV Android SDK 执行。
 */
class KRMMKVModule : KuiklyRenderBaseModule() {

    override fun call(method: String, params: String?, callback: KuiklyRenderCallback?): Any? {
        return when (method) {
            "encode" -> encode(params)
            "decode" -> decode(params)
            "containsKey" -> containsKey(params)
            "removeValueForKey" -> removeValueForKey(params)
            "removeValuesForKeys" -> removeValuesForKeys(params)
            "allKeys" -> allKeys(params, callback)
            "count" -> count(params)
            "totalSize" -> totalSize(params)
            "actualSize" -> actualSize(params)
            "clearAll" -> clearAll(params)
            "trim" -> trim(params)
            "batchEncode" -> batchEncode(params)
            else -> null
        }
    }

    private fun getMMKV(mmapID: String?): MMKV {
        return if (mmapID.isNullOrEmpty()) {
            MMKV.defaultMMKV()
        } else {
            MMKV.mmkvWithID(mmapID)
        }
    }

    private fun encode(params: String?): Any? {
        if (params == null) return null
        val json = JSONObject(params)
        val key = json.optString("key")
        val type = json.optString("type")
        val mmapID = json.optString("mmapID", null)
        val mmkv = getMMKV(mmapID)

        when (type) {
            "string" -> mmkv.encode(key, json.optString("value"))
            "int" -> mmkv.encode(key, json.optInt("value"))
            "long" -> mmkv.encode(key, json.optLong("value"))
            "float" -> mmkv.encode(key, json.optDouble("value").toFloat())
            "double" -> mmkv.encode(key, json.optDouble("value"))
            "bool" -> mmkv.encode(key, json.optBoolean("value"))
            "bytes" -> {
                val base64Str = json.optString("value")
                val bytes = android.util.Base64.decode(base64Str, android.util.Base64.DEFAULT)
                mmkv.encode(key, bytes)
            }
        }
        return null
    }

    private fun decode(params: String?): String? {
        if (params == null) return null
        val json = JSONObject(params)
        val key = json.optString("key")
        val type = json.optString("type")
        val mmapID = json.optString("mmapID", null)
        val mmkv = getMMKV(mmapID)

        return when (type) {
            "string" -> mmkv.decodeString(key, json.optString("defaultValue", "")) ?: json.optString("defaultValue", "")
            "int" -> mmkv.decodeInt(key, json.optInt("defaultValue", 0)).toString()
            "long" -> mmkv.decodeLong(key, json.optLong("defaultValue", 0L)).toString()
            "float" -> mmkv.decodeFloat(key, json.optDouble("defaultValue", 0.0).toFloat()).toString()
            "double" -> mmkv.decodeDouble(key, json.optDouble("defaultValue", 0.0)).toString()
            "bool" -> mmkv.decodeBool(key, json.optBoolean("defaultValue", false)).toString()
            "bytes" -> {
                val bytes = mmkv.decodeBytes(key)
                if (bytes != null) {
                    android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
                } else ""
            }
            else -> ""
        }
    }

    private fun containsKey(params: String?): String? {
        if (params == null) return "false"
        val json = JSONObject(params)
        val key = json.optString("key")
        val mmapID = json.optString("mmapID", null)
        return getMMKV(mmapID).containsKey(key).toString()
    }

    private fun removeValueForKey(params: String?): Any? {
        if (params == null) return null
        val json = JSONObject(params)
        val key = json.optString("key")
        val mmapID = json.optString("mmapID", null)
        getMMKV(mmapID).removeValueForKey(key)
        return null
    }

    private fun removeValuesForKeys(params: String?): Any? {
        if (params == null) return null
        val json = JSONObject(params)
        val keysArray = json.optJSONArray("keys") ?: return null
        val keys = Array(keysArray.length()) { keysArray.getString(it) }
        val mmapID = json.optString("mmapID", null)
        getMMKV(mmapID).removeValuesForKeys(keys)
        return null
    }

    private fun allKeys(params: String?, callback: KuiklyRenderCallback?): Any? {
        val json = if (params != null) JSONObject(params) else JSONObject()
        val mmapID = json.optString("mmapID", null)
        val keys = getMMKV(mmapID).allKeys() ?: emptyArray()
        val result = mapOf("keys" to keys.joinToString(","))
        callback?.invoke(result)
        return null
    }

    private fun count(params: String?): String {
        val json = if (params != null) JSONObject(params) else JSONObject()
        val mmapID = json.optString("mmapID", null)
        return getMMKV(mmapID).count().toString()
    }

    private fun totalSize(params: String?): String {
        val json = if (params != null) JSONObject(params) else JSONObject()
        val mmapID = json.optString("mmapID", null)
        return getMMKV(mmapID).totalSize().toString()
    }

    private fun actualSize(params: String?): String {
        val json = if (params != null) JSONObject(params) else JSONObject()
        val mmapID = json.optString("mmapID", null)
        return getMMKV(mmapID).actualSize().toString()
    }

    private fun clearAll(params: String?): Any? {
        val json = if (params != null) JSONObject(params) else JSONObject()
        val mmapID = json.optString("mmapID", null)
        getMMKV(mmapID).clearAll()
        return null
    }

    private fun trim(params: String?): Any? {
        val json = if (params != null) JSONObject(params) else JSONObject()
        val mmapID = json.optString("mmapID", null)
        getMMKV(mmapID).trim()
        return null
    }

    private fun batchEncode(params: String?): Any? {
        if (params == null) return null
        val json = JSONObject(params)
        val entries = json.optJSONArray("entries") ?: return null
        val mmapID = json.optString("mmapID", null)
        val mmkv = getMMKV(mmapID)

        for (i in 0 until entries.length()) {
            val entry = entries.getJSONObject(i)
            val key = entry.optString("key")
            val type = entry.optString("type", "string")
            when (type) {
                "string" -> mmkv.encode(key, entry.optString("value"))
                "int" -> mmkv.encode(key, entry.optInt("value"))
                "long" -> mmkv.encode(key, entry.optLong("value"))
                "float" -> mmkv.encode(key, entry.optDouble("value").toFloat())
                "double" -> mmkv.encode(key, entry.optDouble("value"))
                "bool" -> mmkv.encode(key, entry.optBoolean("value"))
            }
        }
        return null
    }

    companion object {
        const val MODULE_NAME = "KuiklyMMKVModule"
    }
}
