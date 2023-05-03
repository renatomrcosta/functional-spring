package com.xunfos.functionalspring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FunctionalSpringApplication

fun main(args: Array<String>) {
    runApplication<FunctionalSpringApplication>(*args)
}
