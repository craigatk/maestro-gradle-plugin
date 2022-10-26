package com.atkinsondev.maestro

fun baseBuildFileContents(): String = """
    buildscript {
        repositories {
            gradlePluginPortal()
            mavenCentral()
        }
    }
    
    plugins {
        id "com.atkinsondev.maestro"
    }
    
    repositories {
        mavenCentral()
    }
""".trimIndent()
