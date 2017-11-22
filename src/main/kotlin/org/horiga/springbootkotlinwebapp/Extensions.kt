package org.horiga.springbootkotlinwebapp

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse

fun ServerResponse.BodyBuilder.json() = contentType(MediaType.APPLICATION_JSON_UTF8)
