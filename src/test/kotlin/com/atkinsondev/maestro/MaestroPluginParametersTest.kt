package com.atkinsondev.maestro

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import strikt.api.expectThat
import strikt.assertions.contains
import java.io.File
import java.nio.file.Path

class MaestroPluginParametersTest {

    @Test
    fun `should execute Maestro tests with one parameter`(@TempDir projectRootDirPath: Path) {
        val flowsDir = File(projectRootDirPath.toFile(), "flows")
        flowsDir.mkdirs()

        File(flowsDir, "flow1.yml").writeText(" ")

        val buildFileContents = """
            ${baseBuildFileContents()}
            
            task maestroTest(type: com.atkinsondev.maestro.MaestroTest) {
                flowsDir = file("${flowsDir.absolutePath}")
            
                flowParameters = ["env1": "val1"]
            
                maestroExecutable = "echo"
            }
        """.trimIndent()

        File(projectRootDirPath.toFile(), "build.gradle").writeText(buildFileContents)

        val buildResult = GradleRunner.create()
            .withProjectDir(projectRootDirPath.toFile())
            .withArguments("maestroTest", "--info", "--configuration-cache")
            .withPluginClasspath()
            .build()

        expectThat(buildResult.output).contains("Running Maestro command echo -e env1 val1 test flow1.yml")
    }

    @Test
    fun `should execute Maestro tests with two parameters`(@TempDir projectRootDirPath: Path) {
        val flowsDir = File(projectRootDirPath.toFile(), "flows")
        flowsDir.mkdirs()

        File(flowsDir, "flow1.yml").writeText(" ")

        val buildFileContents = """
            ${baseBuildFileContents()}
            
            task maestroTest(type: com.atkinsondev.maestro.MaestroTest) {
                flowsDir = file("${flowsDir.absolutePath}")
            
                flowParameters = ["env1": "val1", "env2": "val2"]
            
                maestroExecutable = "echo"
            }
        """.trimIndent()

        File(projectRootDirPath.toFile(), "build.gradle").writeText(buildFileContents)

        val buildResult = GradleRunner.create()
            .withProjectDir(projectRootDirPath.toFile())
            .withArguments("maestroTest", "--info", "--configuration-cache")
            .withPluginClasspath()
            .build()

        expectThat(buildResult.output).contains("Running Maestro command echo -e env1 val1 -e env2 val2 test flow1.yml")
    }
}
