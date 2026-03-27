object Version {

    private const val KUIKLY_VERSION = "2.7.0"
    private const val KOTLIN_VERSION = "2.1.21"
    private const val KOTLIN_OHOS_VERSION = "2.0.21-ohos"

    /**
     * 获取 Kuikly 版本号，版本号规则：${shortVersion}-${kotlinVersion}
     * 适用于 core、core-ksp、core-annotation、core-render-android
     */
    fun getKuiklyVersion(): String {
        return "$KUIKLY_VERSION-$KOTLIN_VERSION"
    }

    /**
     * 获取 Kuikly Ohos版本号
     */
    fun getKuiklyOhosVersion(): String {
        return "$KUIKLY_VERSION-$KOTLIN_OHOS_VERSION"
    }
}

/**
 * 获取 Kuikly Core 的 Maven Group ID
 *
 * 通过 gradle.properties 或命令行 -P 参数控制：
 * - 外网版（默认）: com.tencent.kuikly-open
 * - 内网版（动态化）: com.tencent.kuikly
 */
fun org.gradle.api.Project.kuiklyCoreGroup(): String {
    return findProperty("kuiklyCoreGroup") as? String
        ?: findProperty("KUIKLY_CORE_GROUP") as? String
        ?: "com.tencent.kuikly-open"
}

object BuildPlugin {
    /**
     * 注意：Gradle 插件始终使用外网版坐标（插件坐标不受动态化影响）
     */
    val kuikly by lazy {
        "com.tencent.kuikly-open:core-gradle-plugin:${Version.getKuiklyVersion()}"
    }
}