# Maestro Gradle plugin

[Maestro](https://maestro.mobile.dev/) lets you write high-level tests (or "flows" in Maestro terminology) that interact with your app the same way as your users.
Tapping on the screen, entering text using the keyboard, etc.
That way you can verify the different components of your application do work together to deliver the user experience
you're expecting.

Currently the `maestro test` CLI command only supports running one flow YAML file at a time.
I don't know about you, but I'm too lazy to run all my individual test files by hand and would forget to add
tests if I had a list of them to maintain.

Can you use Gradle to run all your Maestro tests in a single Gradle command?

Yes! That's what this plugin provides - a Gradle task to conveniently run all your Maestro tests.

Note: this is an unofficial plugin with no association with mobile.dev

## Usage

The plugin provides a new task type `com.atkinsondev.maestro.MaestroTestsTask` that lets you seamlessly run
all your Maestro flow files in a given directory.

To use it, first add the plugin to your `build.gradle` file's `plugins` block:

```
plugins {
  id 'com.atkinsondev.maestro' version "1.0.0"
}
```

Next, add a task to your build file such as the following - replacing "src/maestro/flows" with the directory containing your flow files:

```groovy
task maestroTests(type: com.atkinsondev.maestro.MaestroTestsTask) {
    flowsDir = file("src/maestro/flows")
}
```

Now you can run all your Maestro tests with a single Gradle command:

```shell
./gradlew maestroTests
```

### Screenshots on test failure

The more information you have about a test failure, the faster it is to diagnose what went wrong.
In the browser testing world, it's common to take screenshots when a test fails when using a tool like Cypress.

Maestro doesn't yet have that type of capability, but this Gradle Maestro task supports it with an additional parameter:

```groovy
task maestroTests(type: com.atkinsondev.maestro.MaestroTestsTask) {
    flowsDir = file("src/maestro/flows")

    screenshotFlowFile = file("src/maestro/screenshot.yml")
}
```

And then create a `screenshot.yml` file similar to:

```yaml
appId: com.atkinsondev.weeklygoals
---
- takeScreenshot: build/maestro-failure-screenshot
```

Now when a test fails, the task will take a screenshot of the current app screen and place it in the file `build/maestro-failure-screenshot.png`

### All configuration options

| Parameter          | Type                | Default                          | Description                                                     |
| ------------------ | ------------------- | -------------------------------- | --------------------------------------------------------------- |
| flowsDir**         | `File`              | `null`                           | Directory containing your Maestro test flow YAML files          |
| screenshotFlowFile | `File`              | `null`                           | Headers to pass to the OpenTelemetry server, such as an API key |

** _Required_

## Compatibility

The plugin is compatible with Gradle versions `6.1.1` and higher.

## Changelog

* 1.0.0
    * Initial release
