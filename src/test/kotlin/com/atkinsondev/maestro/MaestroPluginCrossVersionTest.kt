package com.atkinsondev.maestro

import org.gradle.testkit.runner.GradleRunner
import org.gradle.util.GradleVersion
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.contains
import java.io.File
import java.nio.file.Path
import java.util.stream.Stream

class MaestroPluginCrossVersionTest {

    @ParameterizedTest
    @MethodSource("gradleVersions")
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

        // Configuration Cache was introduced in Gradle 6.6, don't try to enable it on older versions.
        val arguments = arrayListOf("maestroTest", "--info", "--stacktrace")
        if (GradleVersion.version(gradleVersion) >= GradleVersion.version("6.6")) {
            arguments.add("--configuration-cache")
        }

        val buildResult = GradleRunner.create()
            .withProjectDir(projectRootDirPath.toFile())
            .withArguments(arguments)
            .withGradleVersion(gradleVersion)
            .withPluginClasspath()
            .build()

        expectThat(buildResult.output).contains("Running Maestro command echo test ${flowsDir.absolutePath}")
    }

    companion object {
        @JvmStatic
        fun gradleVersions(): Stream<String> = listOf("6.1.1", "6.9.1", "7.0", "7.5.1", GradleVersion.current().version).stream()
    }
}
