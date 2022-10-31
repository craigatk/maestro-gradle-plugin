package com.atkinsondev.maestro

fun baseBuildFileContents(additionalPlugins: String = ""): String = """
    buildscript {
        repositories {
            gradlePluginPortal()
            mavenCentral()
        }
    }
    
    plugins {
        id "com.atkinsondev.maestro"
        $additionalPlugins
    }
    
    repositories {
        mavenCentral()
    }
""".trimIndent()
