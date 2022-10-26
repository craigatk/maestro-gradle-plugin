package com.atkinsondev.maestro

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import strikt.api.expectThat
import strikt.assertions.contains
import java.io.File
import java.nio.file.Path

class MaestroPluginCrossVersionTest {
    @ParameterizedTest
    @ValueSource(strings = ["6.1.1", "6.9.1", "7.0", "7.5.1"])
    fun `should run Maestro tests with Gradle version`(gradleVersion: String, @TempDir projectRootDirPath: Path) {
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
            .withArguments("maestroTest", "--info", "--stacktrace")
            .withGradleVersion(gradleVersion)
            .withPluginClasspath()
            .build()

        expectThat(buildResult.output).contains("test flow1.yml")
        expectThat(buildResult.output).contains("test flow2.yml")
        expectThat(buildResult.output).contains("test flow3.yml")
    }
}
