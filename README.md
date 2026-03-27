# KuiklyMMKV

[MMKV](https://github.com/Tencent/MMKV) 是腾讯开源的高性能 Key-Value 存储框架。KuiklyMMKV 基于 Kuikly Module 机制封装原生 MMKV SDK，通过 KMP 跨端层与各平台原生 Module 通信，实现 Android、iOS、鸿蒙三端统一的 KV 存储能力。



## 快速开始

###  跨端侧

#### 添加仓库

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/")
        }
    }
}
```

#### 添加依赖

```kotlin
sourceSets {
    val commonMain by getting {
        dependencies {
            implementation("com.tencent.kuikly-open:core:${Version.getKuiklyVersion()}")
            implementation("com.tencent.kuikly-open:core-annotations:${Version.getKuiklyVersion()}")
            // KuiklyMMKV 跨端模块
            implementation("com.tencent.kuiklybase:KuiklyMMKV:0.0.1")
        }
    }
}
```

###  Android 原生侧

#### 添加依赖

在 `androidApp/build.gradle.kts` 中添加依赖：

```kotlin
dependencies {
    implementation("com.tencent.kuiklybase:KuiklyMMKVAndroid:0.0.1-2.0.21")
}
```

#### 注册 Module

在 Kuikly 的 `registerExternalModule` 中注册：

```kotlin
import com.tencent.kuiklybase.mmkv.KRMMKVModule

override fun registerExternalModule(kuiklyRenderExport: IKuiklyRenderExport) {
    super.registerExternalModule(kuiklyRenderExport)
    with(kuiklyRenderExport) {
        moduleExport(KRMMKVModule.MODULE_NAME) {
            KRMMKVModule()
        }
    }
}
```

###  iOS 原生侧

#### 添加依赖

在 `Podfile` 中添加：

```ruby
pod 'KuiklyMMKVIOS', :git => 'https://github.com/Kuikly-contrib/KuiklyMMKV.git', :branch => 'main'
```

然后执行 `pod install`。


#### Module 注册

iOS 端 Kuikly 框架会通过 ObjC 运行时自动发现继承自 `KRBaseModule` 的类，**无需手动注册**。

###  鸿蒙原生侧

#### 安装 MMKV

通过 ohpm 安装 MMKV SDK：

```bash
ohpm install @tencent/mmkv
```

#### 添加依赖

在鸿蒙项目的 `entry/oh-package.json5` 中添加：

```json5
{
  "dependencies": {
    "kuikly-mmkv-ohos": "file:../KuiklyMMKVOhos",
    "@tencent/mmkv": "2.1.0"
  }
}
```

> **⚠️ 重要**：`@tencent/mmkv` HAR 包内置了 `libmmkv.so`，HarmonyOS 构建系统会通过 HAR 包的 `nativeComponents` 声明自动将其打包到 HAP 中。**请勿**在 `CMakeLists.txt` 中添加 `libmmkv.so` 的链接配置，否则会导致 `libentry.so` 加载失败（表现为 `Cannot read property initKuikly of undefined` 错误）。MMKV 的初始化由 `KRMMKVModule` 在首次调用时自动完成，**无需手动调用 `MMKV.initialize()`**。

#### 注册 Module

在 `KuiklyViewDelegate.ets` 中注册 Module：

```typescript
import { KRMMKVModule } from 'kuikly-mmkv-ohos';

export class KuiklyViewDelegate extends IKuiklyViewDelegate {
  getCustomRenderModuleCreatorRegisterMap(): Map<string, KRRenderModuleExportCreator> {
    const map: Map<string, KRRenderModuleExportCreator> = new Map();
    map.set(KRMMKVModule.MODULE_NAME, () => new KRMMKVModule());
    return map;
  }
}
```

---

## KMP 跨端 API

所有 API 在 `commonMain` 中通过 `MMKVModule` 提供，Android、iOS、鸿蒙三端通用。

### 获取 Module 实例

```kotlin
import com.tencent.kuiklybase.mmkv.MMKVModule

// 方式一：通过 acquireModule（在 Pager 中）
val mmkv: MMKVModule = acquireModule(MMKVModule.MODULE_NAME)

// 方式二：通过便捷扩展属性（在任意 IPagerId 上下文中）
import com.tencent.kuiklybase.mmkv.mmkvModule

val mmkv = mmkvModule  // 等效于 acquireModule<MMKVModule>(MMKVModule.MODULE_NAME)
```

### 数据写入（Encode）

```kotlin
// String
mmkv.encodeString("name", "Kuikly")

// Int
mmkv.encodeInt("age", 3)

// Long
mmkv.encodeLong("timestamp", System.currentTimeMillis())

// Float
mmkv.encodeFloat("score", 9.5f)

// Double
mmkv.encodeDouble("pi", 3.141592653589793)

// Boolean
mmkv.encodeBool("active", true)

// ByteArray（以 Base64 编码字符串传入）
mmkv.encodeBytes("avatar", base64EncodedString)
```

### 数据读取（Decode）

所有读取方法均为**同步调用**，直接返回结果：

```kotlin
val name: String = mmkv.decodeString("name", defaultValue = "")
val age: Int = mmkv.decodeInt("age", defaultValue = 0)
val timestamp: Long = mmkv.decodeLong("timestamp", defaultValue = 0L)
val score: Float = mmkv.decodeFloat("score", defaultValue = 0f)
val pi: Double = mmkv.decodeDouble("pi", defaultValue = 0.0)
val active: Boolean = mmkv.decodeBool("active", defaultValue = false)
val avatar: String = mmkv.decodeBytes("avatar")  // 返回 Base64 编码字符串
```

### 多实例管理

所有方法都支持 `mmapID` 参数，用于指定不同的 MMKV 实例：

```kotlin
// 写入到 "user_store" 实例
mmkv.encodeString("token", "abc123", mmapID = "user_store")

// 从 "user_store" 实例读取
val token = mmkv.decodeString("token", mmapID = "user_store")

// 不指定 mmapID 时使用默认实例
mmkv.encodeString("global_key", "value")
```

### 管理操作

```kotlin
// 判断 key 是否存在
val exists: Boolean = mmkv.containsKey("name")

// 删除单个 key
mmkv.removeValueForKey("name")

// 批量删除
mmkv.removeValuesForKeys(listOf("key1", "key2", "key3"))

// 获取所有 key（异步回调）
mmkv.allKeys { result ->
    val keys = result  // 逗号分隔的 key 列表
}

// 获取所有 key（同步）
val allKeys: String = mmkv.allKeysSync()  // 逗号分隔的字符串

// 获取 key 数量
val keyCount: Long = mmkv.count()

// 获取已使用的存储空间大小（字节）
val total: Long = mmkv.totalSize()

// 获取实际使用的存储空间大小（字节）
val actual: Long = mmkv.actualSize()

// 清除所有数据
mmkv.clearAll()

// 执行 trim（回收空间）
mmkv.trim()
```

### 批量写入

```kotlin
import com.tencent.kuikly.core.nvi.serialization.json.JSONArray
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject

val entries = JSONArray().apply {
    put(JSONObject().apply {
        put("key", "name")
        put("value", "Kuikly")
        put("type", "string")
    })
    put(JSONObject().apply {
        put("key", "count")
        put("value", 100)
        put("type", "int")
    })
}
mmkv.batchEncode(entries)
```

### 支持的数据类型

| 数据类型 | Encode 方法 | Decode 方法 | 默认值 |
|---------|-----------|-----------|-------|
| String | `encodeString()` | `decodeString()` | `""` |
| Int | `encodeInt()` | `decodeInt()` | `0` |
| Long | `encodeLong()` | `decodeLong()` | `0L` |
| Float | `encodeFloat()` | `decodeFloat()` | `0f` |
| Double | `encodeDouble()` | `decodeDouble()` | `0.0` |
| Boolean | `encodeBool()` | `decodeBool()` | `false` |
| ByteArray | `encodeBytes()` | `decodeBytes()` | `""` (Base64) |

---

## 在 Kuikly 页面中使用示例

```kotlin
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.*
import com.tencent.kuiklybase.mmkv.MMKVModule

@Page("mmkv_demo")
class MMKVDemoPage : BasePager() {

    private var readResult by observable("")

    private val mmkv: MMKVModule
        get() = acquireModule(MMKVModule.MODULE_NAME)

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    flex(1f)
                    flexDirection(FlexDirection.COLUMN)
                    alignItems(FlexAlign.CENTER)
                    justifyContent(FlexAlign.CENTER)
                    backgroundColor(Color(0xFFF5F5F5))
                }

                // 写入数据
                Button {
                    attr {
                        text("写入数据")
                        margin(16f)
                    }
                    event {
                        click {
                            ctx.mmkv.encodeString("username", "Kuikly")
                            ctx.mmkv.encodeInt("loginCount", 42)
                            ctx.mmkv.encodeBool("isPremium", true)

                            // 读取数据
                            val name = ctx.mmkv.decodeString("username", "unknown")
                            val count = ctx.mmkv.decodeInt("loginCount", 0)
                            val premium = ctx.mmkv.decodeBool("isPremium", false)
                            ctx.readResult = "用户: $name, 登录次数: $count, 高级会员: $premium"
                        }
                    }
                }

                Text {
                    attr {
                        text(ctx.readResult)
                        fontSize(14f)
                        color(Color(0xFF333333))
                    }
                }
            }
        }
    }
}
```

---