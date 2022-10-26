package com.atkinsondev.maestro

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class MaestroTest : DefaultTask() {
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
        val flowsDirProp = flowsDir.get()
        val maestroExecutable = maestroExecutable.get()

        try {
            val flowsDir = flowsDirProp // File(project.projectDir, flowsDirProp)

            val flowFiles = flowsDir.listFiles()?.filter { it.isFile }

            project.logger.info("Found ${flowFiles?.size ?: 0} flow files in directory ${flowsDir.absolutePath}")

            flowFiles?.forEach { flowFile ->
                project.exec {
                    it.setWorkingDir(flowsDir)

                    it.setCommandLine(maestroExecutable, "test", flowFile.name)
                }
            }
        } catch (e: Exception) {
            val screenshotFlowFile = screenshotFlowFile.orNull

            if (screenshotFlowFile != null) {
                project.logger.info("Maestro tests failed, capturing screenshot with flow file ${screenshotFlowFile.absolutePath}")

                project.exec {
                    it.setCommandLine(maestroExecutable, "test", screenshotFlowFile.absolutePath)
                }
            }

            throw e
        }
    }
}
