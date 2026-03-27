package com.example.kuiklymmkv

import com.example.kuiklymmkv.base.BasePager
import com.example.kuiklymmkv.base.bridgeModule
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.layout.FlexWrap
import com.tencent.kuikly.core.nvi.serialization.json.JSONArray
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.reactive.handler.*
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuiklybase.mmkv.MMKVModule

/**
 * MMKV Demo 页面
 *
 * 综合演示 KuiklyMMKV 组件的全部功能：
 * 1. 基础 CRUD 操作
 * 2. 多数据类型支持
 * 3. 多实例管理
 * 4. 性能基准测试
 */
@Page("mmkv_demo", supportInLocal = true)
internal class MMKVDemoPage : BasePager() {

    // ========== 状态 ==========
    var logs: String by observable("日志输出区域\n")
    var keyInput: String = ""
    var valueInput: String = ""
    var selectedSection: Int by observable(0) // 0=CRUD, 1=数据类型, 2=多实例, 3=性能测试
    var perfResult: String by observable("")

    private val mmkv: MMKVModule
        get() = acquireModule(MMKVModule.MODULE_NAME)

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr {
                backgroundColor(Color(0xFFF5F5F5))
            }

            // 导航栏
            NavBar("KuiklyMMKV Demo")

            // Tab 切换栏
            TabBar(ctx)

            // 内容区
            Scroller {
                attr {
                    flex(1f)
                }

                when (ctx.selectedSection) {
                    0 -> CRUDSection(ctx)
                    1 -> DataTypeSection(ctx)
                    2 -> MultiInstanceSection(ctx)
                    3 -> PerfTestSection(ctx)
                }

                // 日志输出区域
                LogSection(ctx)
            }
        }
    }

    // ========== 功能方法 ==========

    fun appendLog(msg: String) {
        val maxLen = 2000
        val newLogs = logs + "$msg\n"
        logs = if (newLogs.length > maxLen) {
            newLogs.substring(newLogs.length - maxLen)
        } else {
            newLogs
        }
    }

    fun clearLogs() {
        logs = ""
    }

    // ========== CRUD 操作 ==========

    fun doCrudWrite() {
        if (keyInput.isEmpty()) {
            bridgeModule.toast("请输入 Key")
            return
        }
        mmkv.encodeString(keyInput, valueInput)
        appendLog("✅ 写入: $keyInput = $valueInput")
    }

    fun doCrudRead() {
        if (keyInput.isEmpty()) {
            bridgeModule.toast("请输入 Key")
            return
        }
        val result = mmkv.decodeString(keyInput, "<not found>")
        appendLog("📖 读取: $keyInput = $result")
    }

    fun doCrudDelete() {
        if (keyInput.isEmpty()) {
            bridgeModule.toast("请输入 Key")
            return
        }
        mmkv.removeValueForKey(keyInput)
        appendLog("🗑️ 删除: $keyInput")
    }

    fun doCrudContains() {
        if (keyInput.isEmpty()) {
            bridgeModule.toast("请输入 Key")
            return
        }
        val exists = mmkv.containsKey(keyInput)
        appendLog("🔍 包含 $keyInput: $exists")
    }

    fun doCrudClearAll() {
        mmkv.clearAll()
        appendLog("🧹 已清除所有数据")
    }

    fun doCrudShowInfo() {
        val count = mmkv.count()
        val totalSize = mmkv.totalSize()
        val actualSize = mmkv.actualSize()
        appendLog("📊 统计: count=$count, totalSize=${totalSize}B, actualSize=${actualSize}B")
    }

    fun doCrudAllKeys() {
        mmkv.allKeys { result ->
            val keysStr = result?.optString("keys") ?: "<empty>"
            appendLog("🔑 所有Key: $keysStr")
        }
    }

    // ========== 数据类型测试 ==========

    fun doDataTypeTest() {
        appendLog("--- 多数据类型写入测试 ---")

        // String
        mmkv.encodeString("dt_string", "Hello MMKV!")
        val strVal = mmkv.decodeString("dt_string")
        appendLog("String: Hello MMKV! → $strVal ${if (strVal == "Hello MMKV!") "✅" else "❌"}")

        // Int
        mmkv.encodeInt("dt_int", 42)
        val intVal = mmkv.decodeInt("dt_int")
        appendLog("Int: 42 → $intVal ${if (intVal == 42) "✅" else "❌"}")

        // Long
        mmkv.encodeLong("dt_long", 9876543210L)
        val longVal = mmkv.decodeLong("dt_long")
        appendLog("Long: 9876543210 → $longVal ${if (longVal == 9876543210L) "✅" else "❌"}")

        // Float
        mmkv.encodeFloat("dt_float", 3.14f)
        val floatVal = mmkv.decodeFloat("dt_float")
        appendLog("Float: 3.14 → $floatVal ${if (kotlin.math.abs(floatVal - 3.14f) < 0.01f) "✅" else "❌"}")

        // Double
        mmkv.encodeDouble("dt_double", 2.718281828)
        val doubleVal = mmkv.decodeDouble("dt_double")
        appendLog("Double: 2.718281828 → $doubleVal ${if (kotlin.math.abs(doubleVal - 2.718281828) < 0.0001) "✅" else "❌"}")

        // Boolean
        mmkv.encodeBool("dt_bool_t", true)
        mmkv.encodeBool("dt_bool_f", false)
        val boolT = mmkv.decodeBool("dt_bool_t")
        val boolF = mmkv.decodeBool("dt_bool_f")
        appendLog("Bool(true): $boolT ${if (boolT) "✅" else "❌"}")
        appendLog("Bool(false): $boolF ${if (!boolF) "✅" else "❌"}")

        appendLog("--- 数据类型测试完成 ---")
    }

    fun doDefaultValueTest() {
        appendLog("--- 默认值测试 ---")
        val s = mmkv.decodeString("nonexist_str", "default")
        appendLog("String默认值: $s ${if (s == "default") "✅" else "❌"}")
        val i = mmkv.decodeInt("nonexist_int", -1)
        appendLog("Int默认值: $i ${if (i == -1) "✅" else "❌"}")
        val b = mmkv.decodeBool("nonexist_bool", true)
        appendLog("Bool默认值: $b ${if (b) "✅" else "❌"}")
        appendLog("--- 默认值测试完成 ---")
    }

    // ========== 多实例测试 ==========

    fun doMultiInstanceTest() {
        val storeA = "store_alpha"
        val storeB = "store_beta"

        appendLog("--- 多实例测试 ---")

        // 先清理
        mmkv.clearAll(storeA)
        mmkv.clearAll(storeB)

        // 写入不同实例
        mmkv.encodeString("name", "Alice", mmapID = storeA)
        mmkv.encodeString("name", "Bob", mmapID = storeB)
        mmkv.encodeInt("score", 100, mmapID = storeA)
        mmkv.encodeInt("score", 200, mmapID = storeB)

        // 从不同实例读取
        val nameA = mmkv.decodeString("name", "", mmapID = storeA)
        val nameB = mmkv.decodeString("name", "", mmapID = storeB)
        val scoreA = mmkv.decodeInt("score", 0, mmapID = storeA)
        val scoreB = mmkv.decodeInt("score", 0, mmapID = storeB)

        appendLog("Store-A name: $nameA ${if (nameA == "Alice") "✅" else "❌"}")
        appendLog("Store-B name: $nameB ${if (nameB == "Bob") "✅" else "❌"}")
        appendLog("Store-A score: $scoreA ${if (scoreA == 100) "✅" else "❌"}")
        appendLog("Store-B score: $scoreB ${if (scoreB == 200) "✅" else "❌"}")

        // 统计信息
        val countA = mmkv.count(storeA)
        val countB = mmkv.count(storeB)
        appendLog("Store-A count: $countA, Store-B count: $countB")

        // 清理一个实例不影响另一个
        mmkv.clearAll(storeA)
        val nameAAfterClear = mmkv.decodeString("name", "<cleared>", mmapID = storeA)
        val nameBAfterClear = mmkv.decodeString("name", "<cleared>", mmapID = storeB)
        appendLog("Clear A后: A.name=$nameAAfterClear, B.name=$nameBAfterClear")
        appendLog("隔离性: ${if (nameAAfterClear == "<cleared>" && nameBAfterClear == "Bob") "✅ 通过" else "❌ 失败"}")

        // 清理
        mmkv.clearAll(storeB)
        appendLog("--- 多实例测试完成 ---")
    }

    // ========== 性能测试 ==========

    fun doPerfTest(count: Int) {
        appendLog("--- 性能测试 (${count}次) ---")
        val startTime = bridgeModule.currentTimeStamp()

        // 写入测试
        for (i in 0 until count) {
            mmkv.encodeString("perf_key_$i", "perf_value_$i")
        }
        val writeTime = bridgeModule.currentTimeStamp()
        val writeCost = writeTime - startTime
        appendLog("写入 ${count} 条: ${writeCost}ms (${if (count > 0) writeCost * 1000 / count else 0}μs/条)")

        // 读取测试
        val readStart = bridgeModule.currentTimeStamp()
        for (i in 0 until count) {
            mmkv.decodeString("perf_key_$i")
        }
        val readTime = bridgeModule.currentTimeStamp()
        val readCost = readTime - readStart
        appendLog("读取 ${count} 条: ${readCost}ms (${if (count > 0) readCost * 1000 / count else 0}μs/条)")

        // 删除测试
        val delStart = bridgeModule.currentTimeStamp()
        for (i in 0 until count) {
            mmkv.removeValueForKey("perf_key_$i")
        }
        val delTime = bridgeModule.currentTimeStamp()
        val delCost = delTime - delStart
        appendLog("删除 ${count} 条: ${delCost}ms (${if (count > 0) delCost * 1000 / count else 0}μs/条)")

        val totalCost = delTime - startTime
        appendLog("总耗时: ${totalCost}ms")

        perfResult = "写:${writeCost}ms 读:${readCost}ms 删:${delCost}ms 总:${totalCost}ms"
        appendLog("--- 性能测试完成 ---")
    }

    companion object {
        // 颜色主题
        val PRIMARY_COLOR = Color(0xFF6200EE)
        val PRIMARY_DARK = Color(0xFF3700B3)
        val ACCENT_COLOR = Color(0xFF03DAC5)
        val SURFACE_COLOR = Color(0xFFFFFFFF)
        val BG_COLOR = Color(0xFFF5F5F5)
        val TEXT_PRIMARY = Color(0xFF212121)
        val TEXT_SECONDARY = Color(0xFF757575)
        val SUCCESS_COLOR = Color(0xFF4CAF50)
        val ERROR_COLOR = Color(0xFFF44336)
        val WARNING_COLOR = Color(0xFFFF9800)
    }
}

// ==================== UI 组件 ====================

/**
 * 导航栏
 */
private fun ViewContainer<*, *>.NavBar(title: String) {
    View {
        attr {
            paddingTop(getPager().pageData.statusBarHeight)
            backgroundColor(MMKVDemoPage.PRIMARY_COLOR)
        }
        View {
            attr {
                height(48f)
                allCenter()
            }
            Text {
                attr {
                    text(title)
                    fontSize(18f)
                    fontWeightBold()
                    color(Color.WHITE)
                }
            }
        }
    }
}

/**
 * Tab 切换栏
 */
private fun ViewContainer<*, *>.TabBar(ctx: MMKVDemoPage) {
    val tabs = listOf("CRUD", "数据类型", "多实例", "性能测试")
    View {
        attr {
            flexDirectionRow()
            backgroundColor(Color.WHITE)
        }
        tabs.forEachIndexed { index, tabTitle ->
            val isSelected = ctx.selectedSection == index
            View {
                attr {
                    flex(1f)
                    height(44f)
                    allCenter()
                    if (isSelected) {
                        borderBottom(Border(2f, BorderStyle.SOLID, MMKVDemoPage.PRIMARY_COLOR))
                    }
                }
                Text {
                    attr {
                        text(tabTitle)
                        fontSize(13f)
                        if (isSelected) {
                            color(MMKVDemoPage.PRIMARY_COLOR)
                            fontWeightBold()
                        } else {
                            color(MMKVDemoPage.TEXT_SECONDARY)
                        }
                    }
                }
                event {
                    click {
                        ctx.selectedSection = index
                    }
                }
            }
        }
    }
}

// ==================== CRUD Section ====================

private fun ViewContainer<*, *>.CRUDSection(ctx: MMKVDemoPage) {
    SectionCard("基础 CRUD 操作") {
        // Key 输入
        InputRow("Key") { ctx.keyInput = it }
        // Value 输入
        InputRow("Value") { ctx.valueInput = it }

        // 操作按钮组
        View {
            attr {
                flexDirectionRow()
                flexWrap(FlexWrap.WRAP)
                marginTop(10f)
            }
            ActionButton("写入", MMKVDemoPage.PRIMARY_COLOR) { ctx.doCrudWrite() }
            ActionButton("读取", MMKVDemoPage.ACCENT_COLOR) { ctx.doCrudRead() }
            ActionButton("删除", MMKVDemoPage.ERROR_COLOR) { ctx.doCrudDelete() }
            ActionButton("检查", MMKVDemoPage.WARNING_COLOR) { ctx.doCrudContains() }
        }
        View {
            attr {
                flexDirectionRow()
                flexWrap(FlexWrap.WRAP)
                marginTop(6f)
            }
            ActionButton("所有Key", Color(0xFF607D8B)) { ctx.doCrudAllKeys() }
            ActionButton("统计信息", Color(0xFF795548)) { ctx.doCrudShowInfo() }
            ActionButton("清空全部", Color(0xFF9E9E9E)) { ctx.doCrudClearAll() }
        }
    }
}

// ==================== 数据类型 Section ====================

private fun ViewContainer<*, *>.DataTypeSection(ctx: MMKVDemoPage) {
    SectionCard("多数据类型测试") {
        Text {
            attr {
                text("测试 String/Int/Long/Float/Double/Boolean 六种数据类型的读写正确性")
                fontSize(13f)
                color(MMKVDemoPage.TEXT_SECONDARY)
            }
        }
        View {
            attr {
                flexDirectionRow()
                marginTop(12f)
            }
            ActionButton("运行类型测试", MMKVDemoPage.PRIMARY_COLOR) { ctx.doDataTypeTest() }
            ActionButton("默认值测试", MMKVDemoPage.ACCENT_COLOR) { ctx.doDefaultValueTest() }
        }
    }
}

// ==================== 多实例 Section ====================

private fun ViewContainer<*, *>.MultiInstanceSection(ctx: MMKVDemoPage) {
    SectionCard("多实例隔离测试") {
        Text {
            attr {
                text("创建两个独立 MMKV 实例(store_alpha / store_beta)，验证数据隔离性")
                fontSize(13f)
                color(MMKVDemoPage.TEXT_SECONDARY)
            }
        }
        View {
            attr {
                marginTop(12f)
            }
            ActionButton("运行多实例测试", MMKVDemoPage.PRIMARY_COLOR) { ctx.doMultiInstanceTest() }
        }
    }
}

// ==================== 性能测试 Section ====================

private fun ViewContainer<*, *>.PerfTestSection(ctx: MMKVDemoPage) {
    SectionCard("性能基准测试") {
        Text {
            attr {
                text("测量 MMKV 的读/写/删除性能")
                fontSize(13f)
                color(MMKVDemoPage.TEXT_SECONDARY)
            }
        }
        View {
            attr {
                flexDirectionRow()
                flexWrap(FlexWrap.WRAP)
                marginTop(12f)
            }
            ActionButton("100次", Color(0xFF4CAF50)) { ctx.doPerfTest(100) }
            ActionButton("500次", MMKVDemoPage.WARNING_COLOR) { ctx.doPerfTest(500) }
            ActionButton("1000次", MMKVDemoPage.PRIMARY_COLOR) { ctx.doPerfTest(1000) }
            ActionButton("5000次", MMKVDemoPage.ERROR_COLOR) { ctx.doPerfTest(5000) }
        }
        if (ctx.perfResult.isNotEmpty()) {
            View {
                attr {
                    marginTop(10f)
                    padding(8f)
                    borderRadius(6f)
                    backgroundColor(Color(0xFFE8F5E9))
                }
                Text {
                    attr {
                        text("📊 ${ctx.perfResult}")
                        fontSize(13f)
                        color(MMKVDemoPage.SUCCESS_COLOR)
                        fontWeightBold()
                    }
                }
            }
        }
    }
}

// ==================== 日志区域 ====================

private fun ViewContainer<*, *>.LogSection(ctx: MMKVDemoPage) {
    View {
        attr {
            margin(12f)
            padding(12f)
            borderRadius(8f)
            backgroundColor(Color(0xFF263238))
            minHeight(150f)
        }
        View {
            attr {
                flexDirectionRow()
                justifyContentSpaceBetween()
                marginBottom(8f)
            }
            Text {
                attr {
                    text("📋 运行日志")
                    fontSize(14f)
                    color(Color(0xFF80CBC4))
                    fontWeightBold()
                }
            }
            View {
                attr {
                    paddingLeft(10f)
                    paddingRight(10f)
                    paddingTop(4f)
                    paddingBottom(4f)
                    borderRadius(4f)
                    backgroundColor(Color(0xFF37474F))
                }
                Text {
                    attr {
                        text("清空")
                        fontSize(12f)
                        color(Color(0xFFEF9A9A))
                    }
                }
                event {
                    click { ctx.clearLogs() }
                }
            }
        }
        View {
            attr {
                width(pagerData.pageViewWidth - 48f)
            }
            Text {
                attr {
                    text(ctx.logs)
                    fontSize(11f)
                    color(Color(0xFFB0BEC5))
                    lineHeight(18f)
                }
            }
        }
    }
}

// ==================== 通用 UI 组件 ====================

/**
 * 卡片容器
 */
private fun ViewContainer<*, *>.SectionCard(
    title: String,
    content: ViewBuilder
) {
    View {
        attr {
            margin(12f)
            marginBottom(0f)
            padding(16f)
            borderRadius(10f)
            backgroundColor(Color.WHITE)
        }
        Text {
            attr {
                text(title)
                fontSize(16f)
                fontWeightBold()
                color(MMKVDemoPage.TEXT_PRIMARY)
                marginBottom(10f)
            }
        }
        content()
    }
}

/**
 * 输入行
 */
private fun ViewContainer<*, *>.InputRow(label: String, onTextChange: (String) -> Unit) {
    View {
        attr {
            flexDirectionRow()
            alignItemsCenter()
            marginTop(8f)
            height(40f)
        }
        Text {
            attr {
                text(label)
                fontSize(14f)
                color(MMKVDemoPage.TEXT_PRIMARY)
                width(50f)
            }
        }
        View {
            attr {
                flex(1f)
                height(36f)
                borderRadius(6f)
                border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                backgroundColor(Color(0xFFFAFAFA))
            }
            Input {
                attr {
                    flex(1f)
                    fontSize(14f)
                    color(MMKVDemoPage.TEXT_PRIMARY)
                    marginLeft(8f)
                    marginRight(8f)
                    placeholder("输入 $label")
                    placeholderColor(Color(0xFFBDBDBD))
                }
                event {
                    textDidChange {
                        onTextChange(it.text)
                    }
                }
            }
        }
    }
}

/**
 * 操作按钮
 */
private fun ViewContainer<*, *>.ActionButton(
    title: String,
    bgColor: Color,
    onClick: () -> Unit
) {
    View {
        attr {
            paddingLeft(14f)
            paddingRight(14f)
            paddingTop(8f)
            paddingBottom(8f)
            borderRadius(6f)
            backgroundColor(bgColor)
            marginRight(8f)
            marginBottom(6f)
        }
        Text {
            attr {
                text(title)
                fontSize(13f)
                color(Color.WHITE)
                fontWeightBold()
            }
        }
        event {
            click { onClick() }
        }
    }
}
