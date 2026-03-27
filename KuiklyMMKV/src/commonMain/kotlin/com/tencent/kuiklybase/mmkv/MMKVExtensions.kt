package com.tencent.kuiklybase.mmkv

import com.tencent.kuikly.core.base.IPagerId
import com.tencent.kuikly.core.base.pagerId
import com.tencent.kuikly.core.manager.PagerManager

/**
 * 在任意 IPagerId 上下文中便捷获取 MMKVModule
 *
 * 使用示例:
 * ```kotlin
 * // 在 Pager / ComposeView 中
 * mmkvModule.encodeString("key", "value")
 * val value = mmkvModule.decodeString("key")
 * ```
 */
val IPagerId.mmkvModule: MMKVModule by pagerId {
    PagerManager.getPager(it).acquireModule<MMKVModule>(MMKVModule.MODULE_NAME)
}
