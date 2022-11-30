# Maestro Gradle plugin

[Maestro](https://maestro.mobile.dev/) lets you write high-level tests (or "flows" in Maestro terminology) that interact
with your app the same way as your users.
Tapping on the screen, entering text using the keyboard, etc.
That way you can verify the different components of your application do work together to deliver the user experience
you're expecting.

This plugin provides a Gradle task to conveniently run all your Maestro tests and integrate them with
your Gradle build.

Note: this is an unofficial plugin with no association with mobile.dev

## Usage

The plugin provides a new task type `com.atkinsondev.maestro.MaestroTest` that lets you seamlessly run
all your Maestro flow files in a given directory.

To use it, first add the plugin to your `build.gradle` file's `plugins` block:

```
plugins {
  id 'com.atkinsondev.maestro' version "2.0.0"
}
```

Please see the Gradle plugin portal for the latest version: https://plugins.gradle.org/plugin/com.atkinsondev.maestro

Next, add a task to your build file such as the following - replacing "src/maestro/flows" with the directory containing
your flow files:

```groovy
task maestroTest(type: com.atkinsondev.maestro.MaestroTest) {
    flowsDir = file("src/maestro/flows")
}
```

Then you can run all your Maestro tests with a single Gradle command:

```shell
./gradlew maestroTest
```

And now that you have a Gradle task for running your Maestro tests, you can integrate your Maestro tests in your
existing Gradle build lifecycle.

For example, to run your Maestro tests as part of the `check` task and ensure your tests aren't run until the
updated app is built, you can add this to your `build.gradle` file:

```groovy
maestroTest.dependsOn('installDebugAndroidTest')

check.dependsOn('maestroTest')
```

### Generate test report

Masetro supports generating test reports starting in Maestro version `1.15.0` - [Maestro test report docs](https://maestro.mobile.dev/cli/test-suites-and-reports).

To generate a JUnit report, set the plugin parameter `generateJunitReport` to `true`:

```groovy
task maestroTest(type: com.atkinsondev.maestro.MaestroTest) {
    flowsDir = file("src/maestro/flows")

    generateJunitReport = true
}
```

You can also set a specific file name and path for the generated report using the `junitReportFile` parameter:

```groovy
task maestroTest(type: com.atkinsondev.maestro.MaestroTest) {
    flowsDir = file("src/maestro/flows")

    generateJunitReport = true
    junitReportFile = file("build/maestro-report.xml")
}
```

### Flow parameters

Maestro supports passing external parameters to your flows via the `-e` parameter to the `maestro test` command - [Maestro docs](https://maestro.mobile.dev/advanced/parameters-and-constants)

You can specify a map of parameter key/value pairs in the plugin configuration with the `flowParameters` plugin parameter:

```groovy
task maestroTest(type: com.atkinsondev.maestro.MaestroTest) {
    flowsDir = file("src/maestro/flows")

    flowParameters = ["key1": "val1", "key2": "val2"]
}
```

Then the plugin will pass those key/values to the Maestro tests via `-e key1 val1 -e key2 val2`

Note: these parameters will get passed to all flow files in `flowsDir`.

### All configuration options

| Parameter           | Type                  | Default | Description                                                                                                         |
|---------------------|-----------------------|---------|---------------------------------------------------------------------------------------------------------------------|
| flowsDir**          | `File`                |         | Directory containing your Maestro test flow YAML files                                                              |
| flowParameters      | `Map<String, String>` | `null`  | Map of parameter key/values to pass to the Maestro command with the `-e` parameter                                  |
| generateJunitReport | `boolean`             | false   | Whether to pass the `--report junit` parameter to Maestro to generate a JUnit XML report file with the test results |
| junitReportFile     | `File`                | `null`  | Output file for the JUnit report. The Maestro CLI defaults to `report.xml` in the current directory                 | 

** _Required_

## Compatibility

The plugin is compatible with:

* Maestro versions `1.15.0` and higher
* Gradle versions `6.1.1` and higher

## Changelog

* 2.0.0
  * **BREAKING** plugin now requires Maestro 1.15.0 or higher
  * **BREAKING** removing the screenshot-on-failure as it doesn't work when running tests in full directory. Will focus instead on getting the screenshot-on-failure capability into Maestro itself.
  * Adding support for JUnit test report - [Maestro test report docs](https://maestro.mobile.dev/cli/test-suites-and-reports)
  * Switching to use directory support in Maestro 1.15.0 to also support JUnit reports
* 1.2.0
    * Adding support for passing parameters to the Maestro test command with new `flowParameters` plugin parameter
* 1.1.2
    * Publishing with version `1.1.0` of the Gradle publish plugin
* 1.1.1
    * Updating parameter type for `flowsDir` to `InputFiles`
* 1.1.0
    * Adding support for Gradle's configuration cache
* 1.0.1
    * Adding info log of Maestro command that's being run
* 1.0.0
    * Initial release
