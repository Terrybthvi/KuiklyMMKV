package com.tencent.kuiklybase.mmkv

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.tencent.mmkv.MMKV

/**
 * 自动初始化 MMKV 的 ContentProvider
 *
 * 利用 Android ContentProvider 的生命周期特性，在 App 启动时自动完成 MMKV 初始化，
 * 业务方无需在 Application.onCreate() 中手动调用 MMKV.initialize()。
 */
class MMKVInitProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        val ctx = context?.applicationContext ?: context
        if (ctx != null) {
            MMKV.initialize(ctx)
        }
        return true
    }

    override fun query(
        uri: Uri, projection: Array<out String>?, selection: String?,
        selectionArgs: Array<out String>?, sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0
}
