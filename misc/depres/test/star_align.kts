#!/usr/bin/env kotlinc -script -classpath "$mycp"


println("Hello from Kotlin!")
for (arg in args) {
    println("arg: $arg")
}