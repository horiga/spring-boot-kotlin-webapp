package org.horiga.springbootkotlinwebapp.filter

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.HandlerFilterFunction
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class RequestFilter : HandlerFilterFunction<ServerResponse, ServerResponse> {

    companion object {
        val log = LoggerFactory.getLogger(RequestFilter::class.java)!!
    }

    override fun filter(request: ServerRequest?, next: HandlerFunction<ServerResponse>?): Mono<ServerResponse>? {
        try {
            log.info("[Started] RequestFilter#filter")
            return next?.handle(request!!)
        } finally {
            log.info("[Finished] RequestFilter#filter")
        }
    }
}