package org.horiga.springbootkotlinwebapp

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import kotlin.reflect.KClass

//import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBootKotlinWebappApplication

fun runReactiveApplication(type: KClass<*>, vararg args: String) = SpringApplicationBuilder()
        .sources(type.java)
        .web(WebApplicationType.REACTIVE)
        .run(*args)

fun main(args: Array<String>) {

    //runApplication<SpringBootKotlinWebappApplication>(*args)

    //Run with Reactive
    runReactiveApplication(SpringBootKotlinWebappApplication::class, *args)
}
