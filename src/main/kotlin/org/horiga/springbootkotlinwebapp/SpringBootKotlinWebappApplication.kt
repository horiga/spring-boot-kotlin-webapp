package org.horiga.springbootkotlinwebapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBootKotlinWebappApplication

fun main(args: Array<String>) {
    runApplication<SpringBootKotlinWebappApplication>(*args)
}
