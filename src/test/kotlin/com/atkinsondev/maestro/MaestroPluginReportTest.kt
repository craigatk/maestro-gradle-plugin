package com.atkinsondev.maestro

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import strikt.api.expectThat
import strikt.assertions.contains
import java.io.File

class MaestroPluginReportTest {
    @Test
    fun `should support passing JUnit report parameter`(@TempDir projectRootDir: File) {
        val flowsDir = File(projectRootDir, "flows")
        flowsDir.mkdirs()

        File(flowsDir, "flow1.yml").writeText(" ")

        val buildFileContents = """
            ${baseBuildFileContents()}
            
            task maestroTest(type: com.atkinsondev.maestro.MaestroTest) {
                flowsDir = file("${flowsDir.absolutePath}")
            
                generateJunitReport = true
            
                maestroExecutable = "echo"
            }
        """.trimIndent()

        File(projectRootDir, "build.gradle").writeText(buildFileContents)

        val buildResult = GradleRunner.create()
            .withProjectDir(projectRootDir)
            .withArguments("maestroTest", "--info", "--configuration-cache")
            .withPluginClasspath()
            .build()

        expectThat(buildResult.output).contains("Running Maestro command echo test --format junit ${flowsDir.absolutePath}")
    }

    @Test
    fun `should support passing JUnit report parameter and report file name`(@TempDir projectRootDir: File) {
        val flowsDir = File(projectRootDir, "flows")
        flowsDir.mkdirs()

        File(flowsDir, "flow1.yml").writeText(" ")

        val buildFileContents = """
            ${baseBuildFileContents()}
            
            task maestroTest(type: com.atkinsondev.maestro.MaestroTest) {
                flowsDir = file("${flowsDir.absolutePath}")
            
                generateJunitReport = true
                junitReportFile = file("my-report.xml")
            
                maestroExecutable = "echo"
            }
        """.trimIndent()

        File(projectRootDir, "build.gradle").writeText(buildFileContents)

        val buildResult = GradleRunner.create()
            .withProjectDir(projectRootDir)
            .withArguments("maestroTest", "--info", "--configuration-cache")
            .withPluginClasspath()
            .withDebug(true)
            .build()

        expectThat(buildResult.output).contains("Running Maestro command echo test --format junit --output")
        expectThat(buildResult.output).contains("my-report.xml ${flowsDir.absolutePath}")
    }
}
