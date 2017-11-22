package org.horiga.springbootkotlinwebapp.configuration

import org.horiga.springbootkotlinwebapp.handler.UserHandler
import org.horiga.springbootkotlinwebapp.repository.OnMemoryUserRepository
import org.horiga.springbootkotlinwebapp.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class BeanConfiguration {
    @Bean
    fun userRepository(): UserRepository {
        return OnMemoryUserRepository()
    }
}