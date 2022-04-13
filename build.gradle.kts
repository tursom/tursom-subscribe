import cn.tursom.gradle.*
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  repositories {
    maven {
      url = uri("https://nvm.tursom.cn/repository/maven-public/")
    }
  }
  dependencies {
    classpath("cn.tursom:ts-gradle:1.0-SNAPSHOT") { isChanging = true }
  }
  configurations {
    all {
      resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
      resolutionStrategy.cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
    }
  }
}

apply(plugin = "ts-gradle")

plugins {
  kotlin("jvm") version "1.6.20"
  application
}

group = "cn.tursom"
version = "1.0-SNAPSHOT"

repositories {
  // mavenCentral()
  maven {
    url = uri("https://nvm.tursom.cn/repository/maven-public/")
  }
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
  all {
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
    resolutionStrategy.cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
  }
}

dependencies {
  ts_async_http
  ts_coroutine
  ts_ktorm
  ts_log
  ts_web_netty_client
  ts_web_okhttp

  implementation(group = "org.xerial", name = "sqlite-jdbc", version = "3.36.0.3")
  implementation("org.ktorm:ktorm-support-sqlite:3.4.1")
  implementation(group = "com.google.code.gson", name = "gson", version = "2.8.9")
  implementation("com.squareup.okhttp3:okhttp:4.9.3")

  val brotliVersion = "1.7.1"
  implementation(group = "com.aayushatharva.brotli4j", name = "brotli4j", version = brotliVersion)
  listOf("windows-x86_64", "osx-x86_64", "linux-aarch64", "linux-x86_64").forEach { system ->
    implementation(group = "com.aayushatharva.brotli4j", name = "native-$system", version = brotliVersion)
  }
  testImplementation(kotlin("test"))
}

tasks.test {
  useJUnitPlatform()
}

// skip test
if (project.gradle.startParameter.taskNames.firstOrNull { taskName ->
    taskName.endsWith(":test")
  } == null) {
  tasks.withType<Test> {
    enabled = false
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
  kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

application {
  mainClass.set("MainKt")
}