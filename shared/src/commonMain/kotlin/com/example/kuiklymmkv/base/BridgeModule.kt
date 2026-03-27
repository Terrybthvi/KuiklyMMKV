package com.example.kuiklymmkv.base

import com.tencent.kuikly.core.module.CallbackFn
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject

internal class BridgeModule : Module() {

    override fun moduleName(): String {
        return MODULE_NAME
    }

    fun closePage() {
        callNativeMethod(CLOSE_PAGE, null, null)
    }

    fun log(content: String) {
        val methodArgs = JSONObject()
        methodArgs.put("content", content)
        callNativeMethod(LOG, methodArgs, null)
    }

    fun toast(content: String) {
        val methodArgs = JSONObject()
        methodArgs.put("content", content)
        callNativeMethod("toast", methodArgs, null)
    }

    fun openPage(url: String) {
        val methodArgs = JSONObject()
        methodArgs.put("url", url)
        callNativeMethod(OPEN_PAGE, methodArgs, null)
    }

    fun copyToPasteboard(content: String) {
        val methodArgs = JSONObject()
        methodArgs.put("content", content)
        callNativeMethod("copyToPasteboard", methodArgs, null)
    }

    // 同步获取时间戳（毫秒）
    fun currentTimeStamp(): Long {
        val timestamp = syncCallNativeMethod(CURRENT_TIMESTAMP, null)
        return if (timestamp.isNotEmpty()) timestamp.toLong() else 0
    }

    private fun callNativeMethod(methodName: String, data: JSONObject?, callbackFn: CallbackFn?) {
        toNative(false, methodName, data?.toString(), callbackFn, false)
    }

    private fun syncCallNativeMethod(methodName: String, data: JSONObject?): String {
        return toNative(false, methodName, data?.toString(), null, true).toString()
    }

    companion object {
        const val MODULE_NAME = "HRBridgeModule"
        const val OPEN_PAGE = "openPage"
        const val CLOSE_PAGE = "closePage"
        const val LOG = "log"
        const val CURRENT_TIMESTAMP = "currentTimestamp"
    }

}
