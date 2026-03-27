package com.tencent.kuiklybase.mmkv

/**
 * MMKV 配置类
 *
 * 用于在原生端初始化 MMKV 时传递配置参数。
 * KMP 侧主要通过 [MMKVModule] 进行操作，此配置供原生端参考。
 */
object MMKVConfig {
    /**
     * MMKV Module 在 Kuikly 中注册的名称
     * 原生端注册 Module 时需使用此名称
     */
    const val MODULE_NAME = MMKVModule.MODULE_NAME

    /**
     * 默认的 MMKV 实例 ID
     * 当不指定 mmapID 时使用默认实例
     */
    const val DEFAULT_MMAP_ID = "kuikly_mmkv_default"
}
