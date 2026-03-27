package com.example.kuiklymmkv

import android.app.Application

class KRApplication : Application() {

    init {
        application = this
    }

    override fun onCreate() {
        super.onCreate()
        // MMKV 由 KuiklyMMKVAndroid 模块通过 ContentProvider 自动初始化，无需手动调用
    }

    companion object {
        lateinit var application: Application
    }
}