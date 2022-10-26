# maestro-gradle-plugin
Gradle plugin for executing Maestro tests

## Usage

### Screenshots on test failure

```groovy
task maestroTest(type: com.atkinsondev.maestro.MaestroTestsTask) {
    flowsDir = file("src/maestro/flows")

    screenshotFlowFile = file("src/maestro/screenshot.yml")
}
```