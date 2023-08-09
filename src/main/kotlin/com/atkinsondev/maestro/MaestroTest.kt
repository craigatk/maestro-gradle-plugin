package com.atkinsondev.maestro

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

abstract class MaestroTest @Inject constructor(
    private val execOperations: ExecOperations
) : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val flowsDir: DirectoryProperty

    @get:Input
    @get:Optional
    abstract val flowParameters: MapProperty<String, String>

    @get:Input
    @get:Optional
    abstract val generateJunitReport: Property<Boolean>

    @get:OutputFile
    @get:Optional
    abstract val junitReportFile: RegularFileProperty

    @get:Input
    abstract val maestroExecutable: Property<String>

    init {
        maestroExecutable.convention("maestro")
    }

    @TaskAction
    fun runTests() {
        val flowsDir = flowsDir.get().asFile

        execOperations.exec {
            val maestroCommandLine = maestroExecutableWithOptionalParameters() +
                listOf("test") +
                junitReportParameters() +
                listOf(flowsDir.absolutePath)

            logger.info("Running Maestro command ${maestroCommandLine.joinToString(" ")}")

            it.commandLine = maestroCommandLine
        }
    }

    private fun maestroExecutableWithOptionalParameters(): List<String> {
        val maestroExecutable = maestroExecutable.get()
        val parameterMap = flowParameters.getOrElse(mapOf())

        val parameters = parameterMap.flatMap {
            listOf("-e", it.key, it.value)
        }

        return listOf(maestroExecutable) + parameters
    }

    private fun junitReportParameters(): List<String> {
        val junitReport = generateJunitReport.getOrElse(false)

        return if (junitReport) {
            val reportFilePath = junitReportFile.orNull?.asFile?.absolutePath

            if (reportFilePath != null) {
                listOf("--format", "junit", "--output", reportFilePath)
            } else {
                listOf("--format", "junit")
            }
        } else {
            listOf()
        }
    }
}
