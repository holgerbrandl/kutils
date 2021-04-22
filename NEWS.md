# Release Notes

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
