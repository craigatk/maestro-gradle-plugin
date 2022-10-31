package com.atkinsondev.maestro

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isEqualTo
import java.io.File
import java.nio.file.Path

class MaestroPluginTest {
    @Test
    fun `should execute multiple Maestro flow files in directory`(@TempDir projectRootDirPath: Path) {
        val flowsDir = File(projectRootDirPath.toFile(), "flows")
        flowsDir.mkdirs()

        File(flowsDir, "flow1.yml").writeText(" ")
        File(flowsDir, "flow2.yml").writeText(" ")
        File(flowsDir, "flow3.yml").writeText(" ")

        val buildFileContents = """
            ${baseBuildFileContents()}
            
            task maestroTest(type: com.atkinsondev.maestro.MaestroTest) {
                flowsDir = file("${flowsDir.absolutePath}")
            
                maestroExecutable = "echo"
            }
        """.trimIndent()

        File(projectRootDirPath.toFile(), "build.gradle").writeText(buildFileContents)

        val buildResult = GradleRunner.create()
            .withProjectDir(projectRootDirPath.toFile())
            .withArguments("maestroTest", "--info", "--stacktrace", "--configuration-cache")
            .withPluginClasspath()
            .build()

        expectThat(buildResult.output).contains("test flow1.yml")
        expectThat(buildResult.output).contains("test flow2.yml")
        expectThat(buildResult.output).contains("test flow3.yml")

        expectThat(buildResult.output).contains("Running Maestro command echo test flow1.yml")
    }

    @Test
    fun `when Maestro tests fail should run screenshot tests`(@TempDir projectRootDirPath: Path) {
        val flowsDir = File(projectRootDirPath.toFile(), "flows")
        flowsDir.mkdirs()

        File(flowsDir, "flow1.yml").writeText(" ")

        val screenshotFlowFile = File(flowsDir, "screenshot.yml")
        screenshotFlowFile.writeText(" ")

        val buildFileContents = """
            ${baseBuildFileContents()}
            
            task maestroTest(type: com.atkinsondev.maestro.MaestroTest) {
                flowsDir = file("${flowsDir.absolutePath}")
                
                screenshotFlowFile = file("${screenshotFlowFile.absolutePath}")
            
                maestroExecutable = "does-not-exist"
            }
        """.trimIndent()

        File(projectRootDirPath.toFile(), "build.gradle").writeText(buildFileContents)

        val buildResult = GradleRunner.create()
            .withProjectDir(projectRootDirPath.toFile())
            .withArguments("maestroTest", "--info", "--stacktrace", "--configuration-cache")
            .withPluginClasspath()
            .buildAndFail()

        expectThat(buildResult.output).contains("Maestro tests failed, capturing screenshot with flow file")
        expectThat(buildResult.output).contains("screenshot.yml")
    }

    @Test
    fun `validatePlugins task should pass`(@TempDir projectRootDirPath: Path) {
        val flowsDir = File(projectRootDirPath.toFile(), "flows")
        flowsDir.mkdirs()

        val buildFileContents = """
            ${baseBuildFileContents(additionalPlugins = """id "java-gradle-plugin"""")}
                        
            task maestroTest(type: com.atkinsondev.maestro.MaestroTest) {
                flowsDir = file("flows")
            
                maestroExecutable = "echo"
            }
        """.trimIndent()

        File(projectRootDirPath.toFile(), "build.gradle").writeText(buildFileContents)

        val buildResult = GradleRunner.create()
            .withProjectDir(projectRootDirPath.toFile())
            .withArguments("validatePlugins", "--info", "--stacktrace", "--configuration-cache")
            .withPluginClasspath()
            .build()

        expectThat(buildResult.task(":validatePlugins")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
    }
}
