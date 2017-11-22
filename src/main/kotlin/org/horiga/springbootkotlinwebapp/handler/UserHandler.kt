package org.horiga.springbootkotlinwebapp.handler

import org.horiga.springbootkotlinwebapp.json
import org.horiga.springbootkotlinwebapp.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.*

@Configuration
class UserRouter(private val handler: UserHandler) {

    @Bean
    fun routes() = router {
        (accept(MediaType.APPLICATION_JSON) and "/api").nest {
            "/user".nest {
                POST("/", handler::add)
                GET("/{id}", handler::findById)
                GET("/members", handler::find)
            }
        }
    }
}

@Component
class UserHandler(private val userRepository: UserRepository) {

    companion object {
        val log = LoggerFactory.getLogger(UserHandler::class.java)!!
    }

    fun add(req: ServerRequest): Mono<ServerResponse> =
            req.bodyToMono(User::class.java).flatMap { message ->
                ServerResponse.ok().json().body(BodyInserters.fromObject(
                        User(UUID.randomUUID().toString().replace("-", ""), message.displayName, message.age)
                                .also { user -> userRepository.add(user) }))
            }

    fun find(req: ServerRequest): Mono<ServerResponse> {
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

        return ServerResponse.ok().json().body(BodyInserters.fromObject(result.sortedBy {
            val sortBy = req.queryParam("sortBy").orElse("id")
            when(sortBy) {
                "displayName" -> it.displayName
                else -> it.id
            }
        }))
    }

    fun findById(req: ServerRequest): Mono<ServerResponse> =
            ServerResponse.ok().json().body(BodyInserters.fromObject(userRepository.get(req.pathVariable("id"))))
}

data class User(
        val id: String,
        val displayName: String,
        val age: Int
)