package org.horiga.springbootkotlinwebapp.handler

import org.hibernate.validator.constraints.Range
import org.horiga.springbootkotlinwebapp.domain.User
import org.horiga.springbootkotlinwebapp.filter.ElapsedLoggingFilter
import org.horiga.springbootkotlinwebapp.filter.RequestFilter
import org.horiga.springbootkotlinwebapp.json
import org.horiga.springbootkotlinwebapp.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.HandlerFilterFunction
import org.springframework.web.reactive.function.server.HandlerFilterFunction.ofResponseProcessor
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.constraints.NotBlank

@Configuration
class UserRouter(private val handler: UserHandler,
                 private val requestFilter: RequestFilter,
                 private val elapsedLoggingFilter: ElapsedLoggingFilter) {

    companion object {
        val log = LoggerFactory.getLogger(UserRouter::class.java)!!
    }

    @Bean
    fun routes(): RouterFunction<ServerResponse>? {

        // Does not works
        val responseLoggingFilter: HandlerFilterFunction<ServerResponse, ServerResponse> = ofResponseProcessor {
            log.info("!!!!! ofResponseProcessor#it.javaClass.canonicalName=${it.javaClass.canonicalName}")
            if (it.statusCode().isError) {
                log.error("HTTP status is error. HTTP.status=${it.statusCode().value()}")
            }
            Mono.just(it)
        }

        return router {
            (accept(MediaType.APPLICATION_JSON) and "/api").nest {
                "/user".nest {
                    POST("/", handler::add)
                    GET("/{id}", handler::findById)
                    GET("/members", handler::find)
                }
            }
        }.filter(requestFilter).filter(elapsedLoggingFilter)
                .filter(responseLoggingFilter)
    }
}

// Does not work validation
data class AddUserMessage(
    @get:NotBlank
    val displayName: String,
    @get:Range(min = 1, max = 999)
    val age: Int)

@Component
class UserHandler(private val userRepository: UserRepository) {

    companion object {
        val log = LoggerFactory.getLogger(UserHandler::class.java)!!
    }

    fun add(req: ServerRequest): Mono<ServerResponse> = try {
        log.info("[Started] UserHandler#add")
        req.bodyToMono(AddUserMessage::class.java).flatMap { message ->
            ServerResponse.ok().json().body(BodyInserters.fromObject(
                    User(UUID.randomUUID().toString().replace("-", ""), message.displayName, message.age)
                            .also { user -> userRepository.add(user) }))
        }
    } finally {
        log.info("[Finished] UserHandler#add")
    }

    fun find(req: ServerRequest): Mono<ServerResponse> = try {
        log.info("[Started] UserHandler#find")
        val result = if (req.queryParam("filterBy").isPresent) {
            userRepository.getAll().filter {
                val filterBy = req.queryParam("filterBy").get()
                when (filterBy) {
                    "displayName" -> {
                        val filterOpt = req.queryParam("filterOption").orElse("eq")
                        when (filterOpt) {
                            "eq" -> it.displayName == (req.queryParam("filterValue").orElse(""))
                            "startsWith" -> it.displayName.startsWith(req.queryParam("filterValue").orElse(""))
                            "contains" -> it.displayName.contains(req.queryParam("filterValue").orElse(""))
                            else -> true
                        }
                    }
                    "age" -> {
                        val filterOpt = req.queryParam("filterOption").orElse("eq")
                        val filterValue: Int = try {
                            req.queryParam("filterValue").get().toInt()
                        } catch (e: NumberFormatException) {
                            -1
                        }
                        when (filterOpt) {
                            "eq" -> it.age == filterValue
                        // age > filterValue
                            "gt" -> it.age > filterValue
                        // age >= filterValue
                            "ge" -> it.age >= filterValue
                        // age < filterValue
                            "lt" -> it.age < filterValue
                        // age <= filterValue
                            "le" -> it.age <= filterValue
                            else -> true
                        }
                    }
                    else -> true
                }
            }
        } else userRepository.getAll()

        ServerResponse.ok().json().body(BodyInserters.fromObject(result.sortedBy {
            val sortBy = req.queryParam("sortBy").orElse("id")
            when (sortBy) {
                "displayName" -> it.displayName
                else -> it.id
            }
        }))
    } finally {
        log.info("[Finished] UserHandler#add")
    }

    fun findById(req: ServerRequest): Mono<ServerResponse> = try {
        log.info("[Started] UserHandler#findBy")
        ServerResponse.ok().json().body(BodyInserters.fromObject(userRepository.get(req.pathVariable("id"))))
    } finally {
        log.info("[Finished] UserHandler#findBy")
    }
}