plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.cyclonedx.bom)
}

group = "com.kirndoubleb"
version = "0.0.1-SNAPSHOT"
description = "Labs"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

// Dependency Locking Configuration
// 주요 Configuration에 대해 의존성 잠금 활성화
configurations {
    listOf(
        "compileClasspath",
        "runtimeClasspath",
        "testCompileClasspath",
        "testRuntimeClasspath"
    ).forEach { configName ->
        named(configName) {
            resolutionStrategy.activateDependencyLocking()
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.spring.boot.mongodb)
    implementation(libs.bundles.spring.boot.observability)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.sentry.spring.boot.starter)
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.resilience4j)

    developmentOnly(libs.spring.boot.docker.compose)
    runtimeOnly(libs.micrometer.registry.prometheus)
    annotationProcessor(libs.spring.boot.configuration.processor)

    testImplementation(libs.bundles.spring.boot.test)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.bundles.testcontainers)
    testImplementation(libs.wiremock)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.platform.launcher)
}

dependencyManagement {
    imports {
        mavenBom(libs.sentry.bom.get().toString())
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
