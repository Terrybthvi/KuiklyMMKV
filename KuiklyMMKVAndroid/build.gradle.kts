plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
}

// 从 Gradle 参数读取发布配置（优先读取命令行 -P 参数，回退到 gradle.properties 大写风格）
val mavenVersion: String = findProperty("mavenVersion") as? String
    ?: findProperty("MAVEN_VERSION") as? String
    ?: "1.0.0"
val groupId: String = findProperty("groupId") as? String
    ?: findProperty("GROUP_ID") as? String
    ?: "com.tencent.kuiklybase"
val mavenRepoUrl: String = findProperty("mavenRepoUrl") as? String
    ?: findProperty("MAVEN_REPO_URL") as? String
    ?: "https://mirrors.tencent.com/repository/maven/kuikly-open/"
val mavenUsername: String = findProperty("mavenUsername") as? String
    ?: findProperty("MAVEN_USERNAME") as? String
    ?: ""
val mavenPassword: String = findProperty("mavenPassword") as? String
    ?: findProperty("MAVEN_PASSWORD") as? String
    ?: ""

group = groupId
version = mavenVersion

android {
    namespace = "com.tencent.kuiklybase.mmkv.android"
    compileSdk = 34
    defaultConfig {
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // MMKV（使用 api 传递依赖）
    api("com.tencent:mmkv:1.3.9")
    // Kuikly Render（提供 KuiklyRenderBaseModule 等基类，groupId 跟随 core 配置）
    implementation("${project.kuiklyCoreGroup()}:core-render-android:${Version.getKuiklyVersion()}")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = this@afterEvaluate.group.toString()
                artifactId = "KuiklyMMKVAndroid"
                version = this@afterEvaluate.version.toString()
            }
        }
        repositories {
            maven {
                url = uri(mavenRepoUrl)
                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        }
    }
}

dependencies {
    // MMKV（使用 api 传递依赖）
    api("com.tencent:mmkv:1.3.9")
    // Kuikly Render（提供 KuiklyRenderBaseModule 等基类，groupId 跟随 core 配置）
    implementation("${project.kuiklyCoreGroup()}:core-render-android:${Version.getKuiklyVersion()}")
}
