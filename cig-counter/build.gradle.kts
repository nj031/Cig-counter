// Top-level build file where you can add configuration options common to all sub-projects/modules.
//
// Dependency versions (Firebase BOM 33.5.1, Compose BOM 2024.09.03, Room 2.6.1,
// Navigation Compose, plugin versions, etc.) are centrally managed via the Gradle
// Version Catalog at gradle/libs.versions.toml and referenced here/in module build
// files through `libs.*` aliases — nothing is hardcoded in this file.
//
// Plugins declared below are applied with `apply false` at the root so that
// exact versions are resolved once (from the version catalog) and made available
// to be applied, without a version, in whichever module actually needs them
// (e.g. app/build.gradle.kts).
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
}

// Plugin resolution (which repositories Gradle downloads plugins from) and
// dependency resolution (which repositories modules resolve libraries from)
// are configured centrally in settings.gradle.kts via the
// `pluginManagement { repositories { ... } }` and
// `dependencyResolutionManagement { repositories { ... } }` blocks. That keeps
// repository config in one place for the whole project instead of duplicated
// per-module, which is the setup this root file assumes is in place.
