package com.tencent.kuiklybase.mmkv

import com.tencent.kuikly.core.module.CallbackFn
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.nvi.serialization.json.JSONArray
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject

/**
 * KuiklyMMKV - MMKV 跨端存储模块
 *
 * 通过 Kuikly Module 机制封装原生 MMKV SDK，提供高性能的键值对存储能力。
 * 支持多数据类型（String/Int/Long/Float/Double/Boolean/ByteArray）、多实例、
 * 加密存储等完整 MMKV 功能。
 *
 * 使用示例:
 * ```kotlin
 * // 获取模块
 * val mmkv = pager.acquireModule<MMKVModule>(MMKVModule.MODULE_NAME)
 *
 * // 写入数据
 * mmkv.encodeString("name", "Kuikly")
 * mmkv.encodeInt("age", 3)
 * mmkv.encodeBool("active", true)
 *
 * // 读取数据
 * val name = mmkv.decodeString("name", "")
 * val age = mmkv.decodeInt("age", 0)
 *
 * // 多实例
 * mmkv.encodeString("key", "value", mmapID = "user_store")
 * ```
 */
class MMKVModule : Module() {

    override fun moduleName(): String = MODULE_NAME

    // ==================== 编码（写入）方法 ====================

    /**
     * 写入 String 值
     * @param key 键名
     * @param value 值
     * @param mmapID 可选，MMKV 实例 ID，默认使用默认实例
     */
    fun encodeString(key: String, value: String, mmapID: String? = null) {
        val params = JSONObject().apply {
            put("key", key)
            put("value", value)
            put("type", "string")
            mmapID?.let { put("mmapID", it) }
        }
        callNativeMethod(METHOD_ENCODE, params, null)
    }

    /**
     * 写入 Int 值
     */
    fun encodeInt(key: String, value: Int, mmapID: String? = null) {
        val params = JSONObject().apply {
            put("key", key)
            put("value", value)
            put("type", "int")
            mmapID?.let { put("mmapID", it) }
        }
        callNativeMethod(METHOD_ENCODE, params, null)
    }

    /**
     * 写入 Long 值
     */
    fun encodeLong(key: String, value: Long, mmapID: String? = null) {
        val params = JSONObject().apply {
            put("key", key)
            put("value", value)
            put("type", "long")
            mmapID?.let { put("mmapID", it) }
        }
        callNativeMethod(METHOD_ENCODE, params, null)
    }

    /**
     * 写入 Float 值
     */
    fun encodeFloat(key: String, value: Float, mmapID: String? = null) {
        val params = JSONObject().apply {
            put("key", key)
            put("value", value.toDouble())
            put("type", "float")
            mmapID?.let { put("mmapID", it) }
        }
        callNativeMethod(METHOD_ENCODE, params, null)
    }

    /**
     * 写入 Double 值
     */
    fun encodeDouble(key: String, value: Double, mmapID: String? = null) {
        val params = JSONObject().apply {
            put("key", key)
            put("value", value)
            put("type", "double")
            mmapID?.let { put("mmapID", it) }
        }
        callNativeMethod(METHOD_ENCODE, params, null)
    }

    /**
     * 写入 Boolean 值
     */
    fun encodeBool(key: String, value: Boolean, mmapID: String? = null) {
        val params = JSONObject().apply {
            put("key", key)
            put("value", value)
            put("type", "bool")
            mmapID?.let { put("mmapID", it) }
        }
        callNativeMethod(METHOD_ENCODE, params, null)
    }

    /**
     * 写入 ByteArray（以 Base64 编码传输）
     */
    fun encodeBytes(key: String, base64Value: String, mmapID: String? = null) {
        val params = JSONObject().apply {
            put("key", key)
            put("value", base64Value)
            put("type", "bytes")
            mmapID?.let { put("mmapID", it) }
        }
        callNativeMethod(METHOD_ENCODE, params, null)
    }

    // ==================== 解码（读取）方法 ====================

    /**
     * 同步读取 String 值
     * @param key 键名
     * @param defaultValue 默认值
     * @param mmapID 可选，MMKV 实例 ID
     * @return 存储的值或默认值
     */
    fun decodeString(key: String, defaultValue: String = "", mmapID: String? = null): String {
        val params = JSONObject().apply {
            put("key", key)
            put("defaultValue", defaultValue)
            put("type", "string")
            mmapID?.let { put("mmapID", it) }
        }
        val result = syncCallNativeMethod(METHOD_DECODE, params)
        return if (result.isNotEmpty()) result else defaultValue
    }

    /**
     * 同步读取 Int 值
     */
    fun decodeInt(key: String, defaultValue: Int = 0, mmapID: String? = null): Int {
        val params = JSONObject().apply {
            put("key", key)
            put("defaultValue", defaultValue)
            put("type", "int")
            mmapID?.let { put("mmapID", it) }
        }
        val result = syncCallNativeMethod(METHOD_DECODE, params)
        return result.toIntOrNull() ?: defaultValue
    }

    /**
     * 同步读取 Long 值
     */
    fun decodeLong(key: String, defaultValue: Long = 0L, mmapID: String? = null): Long {
        val params = JSONObject().apply {
            put("key", key)
            put("defaultValue", defaultValue)
            put("type", "long")
            mmapID?.let { put("mmapID", it) }
        }
        val result = syncCallNativeMethod(METHOD_DECODE, params)
        return result.toLongOrNull() ?: defaultValue
    }

    /**
     * 同步读取 Float 值
     */
    fun decodeFloat(key: String, defaultValue: Float = 0f, mmapID: String? = null): Float {
        val params = JSONObject().apply {
            put("key", key)
            put("defaultValue", defaultValue.toDouble())
            put("type", "float")
            mmapID?.let { put("mmapID", it) }
        }
        val result = syncCallNativeMethod(METHOD_DECODE, params)
        return result.toFloatOrNull() ?: defaultValue
    }

    /**
     * 同步读取 Double 值
     */
    fun decodeDouble(key: String, defaultValue: Double = 0.0, mmapID: String? = null): Double {
        val params = JSONObject().apply {
            put("key", key)
            put("defaultValue", defaultValue)
            put("type", "double")
            mmapID?.let { put("mmapID", it) }
        }
        val result = syncCallNativeMethod(METHOD_DECODE, params)
        return result.toDoubleOrNull() ?: defaultValue
    }

    /**
     * 同步读取 Boolean 值
     */
    fun decodeBool(key: String, defaultValue: Boolean = false, mmapID: String? = null): Boolean {
        val params = JSONObject().apply {
            put("key", key)
            put("defaultValue", defaultValue)
            put("type", "bool")
            mmapID?.let { put("mmapID", it) }
        }
        val result = syncCallNativeMethod(METHOD_DECODE, params)
        return when (result.lowercase()) {
            "true", "1" -> true
            "false", "0" -> false
            else -> defaultValue
        }
    }

    /**
     * 同步读取 ByteArray（返回 Base64 编码字符串）
     */
    fun decodeBytes(key: String, mmapID: String? = null): String {
        val params = JSONObject().apply {
            put("key", key)
            put("type", "bytes")
            mmapID?.let { put("mmapID", it) }
        }
        return syncCallNativeMethod(METHOD_DECODE, params)
    }

    // ==================== 管理方法 ====================

    /**
     * 检查 key 是否存在
     */
    fun containsKey(key: String, mmapID: String? = null): Boolean {
        val params = JSONObject().apply {
            put("key", key)
            mmapID?.let { put("mmapID", it) }
        }
        val result = syncCallNativeMethod(METHOD_CONTAINS_KEY, params)
        return result == "true" || result == "1"
    }

    /**
     * 删除指定 key
     */
    fun removeValueForKey(key: String, mmapID: String? = null) {
        val params = JSONObject().apply {
            put("key", key)
            mmapID?.let { put("mmapID", it) }
        }
        callNativeMethod(METHOD_REMOVE_VALUE_FOR_KEY, params, null)
    }

    /**
     * 批量删除多个 key
     */
    fun removeValuesForKeys(keys: List<String>, mmapID: String? = null) {
        val params = JSONObject().apply {
            val keysArray = JSONArray()
            keys.forEach { keysArray.put(it) }
            put("keys", keysArray)
            mmapID?.let { put("mmapID", it) }
        }
        callNativeMethod(METHOD_REMOVE_VALUES_FOR_KEYS, params, null)
    }

    /**
     * 获取所有 key 列表（异步回调）
     */
    fun allKeys(mmapID: String? = null, callback: CallbackFn) {
        val params = JSONObject().apply {
            mmapID?.let { put("mmapID", it) }
        }
        callNativeMethod(METHOD_ALL_KEYS, params, callback)
    }

    /**
     * 同步获取所有 key 列表（返回逗号分隔的字符串）
     */
    fun allKeysSync(mmapID: String? = null): String {
        val params = JSONObject().apply {
            mmapID?.let { put("mmapID", it) }
        }
        return syncCallNativeMethod(METHOD_ALL_KEYS, params)
    }

    /**
     * 获取存储的 key 数量
     */
    fun count(mmapID: String? = null): Long {
        val params = JSONObject().apply {
            mmapID?.let { put("mmapID", it) }
        }
        val result = syncCallNativeMethod(METHOD_COUNT, params)
        return result.toLongOrNull() ?: 0L
    }

    /**
     * 获取已使用的存储空间大小（字节）
     */
    fun totalSize(mmapID: String? = null): Long {
        val params = JSONObject().apply {
            mmapID?.let { put("mmapID", it) }
        }
        val result = syncCallNativeMethod(METHOD_TOTAL_SIZE, params)
        return result.toLongOrNull() ?: 0L
    }

    /**
     * 获取实际使用的存储空间大小（字节）
     */
    fun actualSize(mmapID: String? = null): Long {
        val params = JSONObject().apply {
            mmapID?.let { put("mmapID", it) }
        }
        val result = syncCallNativeMethod(METHOD_ACTUAL_SIZE, params)
        return result.toLongOrNull() ?: 0L
    }

    /**
     * 清除所有数据
     */
    fun clearAll(mmapID: String? = null) {
        val params = JSONObject().apply {
            mmapID?.let { put("mmapID", it) }
        }
        callNativeMethod(METHOD_CLEAR_ALL, params, null)
    }

    /**
     * 执行 trim（回收空间）
     */
    fun trim(mmapID: String? = null) {
        val params = JSONObject().apply {
            mmapID?.let { put("mmapID", it) }
        }
        callNativeMethod(METHOD_TRIM, params, null)
    }

    // ==================== 批量操作 ====================

    /**
     * 批量写入键值对（同步，用于性能测试）
     * @param entries JSON 格式的键值对数组，如 [{"key":"k1","value":"v1","type":"string"},...]
     * @param mmapID 可选实例 ID
     */
    fun batchEncode(entries: JSONArray, mmapID: String? = null) {
        val params = JSONObject().apply {
            put("entries", entries)
            mmapID?.let { put("mmapID", it) }
        }
        callNativeMethod(METHOD_BATCH_ENCODE, params, null)
    }

    // ==================== 内部通信方法 ====================

    private fun callNativeMethod(methodName: String, data: JSONObject?, callbackFn: CallbackFn?) {
        toNative(false, methodName, data?.toString(), callbackFn, false)
    }

    private fun syncCallNativeMethod(methodName: String, data: JSONObject?): String {
        return toNative(false, methodName, data?.toString(), null, true).toString()
    }

    companion object {
        const val MODULE_NAME = "KuiklyMMKVModule"

        // 编码/解码
        const val METHOD_ENCODE = "encode"
        const val METHOD_DECODE = "decode"

        // 管理
        const val METHOD_CONTAINS_KEY = "containsKey"
        const val METHOD_REMOVE_VALUE_FOR_KEY = "removeValueForKey"
        const val METHOD_REMOVE_VALUES_FOR_KEYS = "removeValuesForKeys"
        const val METHOD_ALL_KEYS = "allKeys"
        const val METHOD_COUNT = "count"
        const val METHOD_TOTAL_SIZE = "totalSize"
        const val METHOD_ACTUAL_SIZE = "actualSize"
        const val METHOD_CLEAR_ALL = "clearAll"
        const val METHOD_TRIM = "trim"

        // 批量
        const val METHOD_BATCH_ENCODE = "batchEncode"
    }
}
