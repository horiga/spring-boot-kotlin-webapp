package org.horiga.springbootkotlinwebapp.configuration

import org.horiga.springbootkotlinwebapp.repository.OnMemoryUserRepository
import org.horiga.springbootkotlinwebapp.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfigurations {
    @Bean
    fun userRepository(): UserRepository {
        return OnMemoryUserRepository()
    }
}