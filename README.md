# Maestro Gradle plugin

Note: this is an unofficial plugin with no association with mobile.dev

## Usage

### Screenshots on test failure

```groovy
task maestroTest(type: com.atkinsondev.maestro.MaestroTestsTask) {
    flowsDir = file("src/maestro/flows")

    screenshotFlowFile = file("src/maestro/screenshot.yml")
}
```

## Compatibility

The plugin is compatible with Gradle versions `6.1.1` and higher.

## Changelog

* 1.0.0
    * Initial release
