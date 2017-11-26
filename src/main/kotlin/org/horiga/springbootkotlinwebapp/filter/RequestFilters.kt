package org.horiga.springbootkotlinwebapp.filter

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.HandlerFilterFunction
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.time.Instant

@Component
class RequestFilter : HandlerFilterFunction<ServerResponse, ServerResponse> {

    companion object {
        val log = LoggerFactory.getLogger(RequestFilter::class.java)!!
    }

    override fun filter(request: ServerRequest?, next: HandlerFunction<ServerResponse>?): Mono<ServerResponse>? {
        log.info("[Started] RequestFilter#filter")
        try {
            return next?.handle(request!!)
        } finally {
            log.info("[Finished] RequestFilter#filter")
        }
    }
}

@Component
class ElapsedLoggingFilter : HandlerFilterFunction<ServerResponse, ServerResponse> {

    companion object {
        val log = LoggerFactory.getLogger(ElapsedLoggingFilter::class.java)!!
    }

    override fun filter(request: ServerRequest?, next: HandlerFunction<ServerResponse>?): Mono<ServerResponse>? {
        log.info("[Started] ElapsedLoggingFilter#filter")
        val start = Instant.now().toEpochMilli()
        try {
            return next?.handle(request!!)
        } finally {
            val elapsedMillis = Instant.now().toEpochMilli() - start
            log.info(">> ${request?.method()} ${request?.path()} done in $elapsedMillis millis.")
        }
    }

}