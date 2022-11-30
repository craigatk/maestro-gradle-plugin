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
    fun `should execute Maestro flow files in directory`(@TempDir projectRootDirPath: Path) {
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

        expectThat(buildResult.output).contains("Running Maestro command echo test ${flowsDir.absolutePath}")
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
