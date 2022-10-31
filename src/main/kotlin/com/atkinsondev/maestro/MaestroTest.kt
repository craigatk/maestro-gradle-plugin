package com.atkinsondev.maestro

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

abstract class MaestroTest @Inject constructor(
    private val execOperations: ExecOperations,
) : DefaultTask() {
    @get:Input
    abstract val flowsDir: Property<File>

    @get:Input
    @get:Optional
    abstract val screenshotFlowFile: Property<File>

    @get:Input
    abstract val maestroExecutable: Property<String>

    init {
        maestroExecutable.convention("maestro")
    }

    @TaskAction
    fun runTests() {
        val flowsDir = flowsDir.get()
        val maestroExecutable = maestroExecutable.get()

        try {
            val flowFiles = flowsDir.listFiles()?.filter { it.isFile }

            project.logger.info("Found ${flowFiles?.size ?: 0} flow files in directory ${flowsDir.absolutePath}")

            flowFiles?.forEach { flowFile ->
                execOperations.exec {
                    it.workingDir = flowsDir

                    val maestroCommandLine = listOf(maestroExecutable, "test", flowFile.name)

                    project.logger.info("Running Maestro command ${maestroCommandLine.joinToString(" ")}")

                    it.commandLine = maestroCommandLine
                }
            }
        } catch (e: Exception) {
            val screenshotFlowFile = screenshotFlowFile.orNull

            if (screenshotFlowFile != null) {
                project.logger.info("Maestro tests failed, capturing screenshot with flow file ${screenshotFlowFile.absolutePath}")

                execOperations.exec {
                    it.setCommandLine(maestroExecutable, "test", screenshotFlowFile.absolutePath)
                }
            }

            throw e
        }
    }
}
