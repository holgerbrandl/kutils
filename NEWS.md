# Release Notes

## 1.1

Modernized dependencies

## 1.0

* Removed all bioinfo utils
* Add `captureOutput()` and `suppressOutput()`
* Cleaned up API
* Modernized build and dependencies
* Changed github namespace to `com.github.holgerbrandl`
* Changed package to `kutils`

## 0.12

Rereleased to maven-central

## 0.11

* Added `MicroBenchmark` for basic profiling

```kotlin
val mb = MicroBenchmark<String>(reps = 10)

// run config a
mb.elapseNano("config a") {
    2 + 2
}

mb.elapseNano("config b") {
    2 + 2
}

mb.printSummary()

```
