package com.github.ghostbustersapm.ghosthouse

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GhostHouseApplication

fun main(args: Array<String>) {
    runApplication<GhostHouseApplication>(*args)
}
