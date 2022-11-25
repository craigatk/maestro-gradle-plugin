package com.atkinsondev.maestro

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

abstract class MaestroTest @Inject constructor(
    private val execOperations: ExecOperations,
) : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val flowsDir: DirectoryProperty

    @get:Input
    @get:Optional
    abstract val screenshotFlowFile: RegularFileProperty

    @get:Input
    @get:Optional
    abstract val flowParameters: MapProperty<String, String>

    @get:Input
    abstract val maestroExecutable: Property<String>

    init {
        maestroExecutable.convention("maestro")
    }

    @TaskAction
    fun runTests() {
        val flowsDir = flowsDir.get().asFile

        try {
            val flowFiles = flowsDir.listFiles()?.filter { it.isFile }

            logger.info("Found ${flowFiles?.size ?: 0} flow files in directory ${flowsDir.absolutePath}")

            flowFiles?.forEach { flowFile ->
                execOperations.exec {
                    it.workingDir = flowsDir

                    val maestroCommandLine = maestroExecutableWithOptionalParameters() + listOf("test", flowFile.name)

                    logger.info("Running Maestro command ${maestroCommandLine.joinToString(" ")}")

                    it.commandLine = maestroCommandLine
                }
            }
        } catch (e: Exception) {
            val screenshotFlowFile = screenshotFlowFile.asFile.orNull

            if (screenshotFlowFile != null) {
                logger.info("Maestro tests failed, capturing screenshot with flow file ${screenshotFlowFile.absolutePath}")

                execOperations.exec {
                    val maestroCommandLine = maestroExecutableWithOptionalParameters() + listOf("test", screenshotFlowFile.absolutePath)

                    it.commandLine = maestroCommandLine
                }
            }

            throw e
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
}
