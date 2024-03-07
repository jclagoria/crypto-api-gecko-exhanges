package ar.com.api.exchanges.exception;

import ar.com.api.exchanges.model.ApiErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalApiExceptionHandler {

    @ExceptionHandler(ApiClientErrorException.class)
    public Mono<Void> handleClientErrorRequestException(ServerWebExchange serverWebExchange,
                                                        ApiClientErrorException apiClientErrorException)
            throws JsonProcessingException {

        log.error("An ApiClientErrorException occurred {}", apiClientErrorException.getMessage());

        ServerHttpResponse response = serverWebExchange.getResponse();
        HttpStatus status = apiClientErrorException.getHttpStatus();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiErrorResponse apiError = ApiErrorResponse
                .builder()
                .code(status.value())
                .message(apiClientErrorException.getMessage())
                .errorMessage(apiClientErrorException.getLocalizedMessage())
                .build();

        return response.writeWith(
                Mono.just(
                        response.bufferFactory().wrap(new ObjectMapper().writeValueAsBytes(apiError))
                ));
    }

    @ExceptionHandler(ApiServeErrorrException.class)
    public Mono<Void> handleServerErrorRequestException(ServerWebExchange serverWebExchange,
                                                        ApiServeErrorrException apiServeErrorrException)
            throws JsonProcessingException {

        log.error("An ApiServerErrorException occurred {}", apiServeErrorrException.getMessage());

        ServerHttpResponse response = serverWebExchange.getResponse();
        HttpStatus status = apiServeErrorrException.getHttpStatus();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiErrorResponse apiErrorResponse = ApiErrorResponse
                .builder()
                .code(status.value())
                .message(apiServeErrorrException.getMessage())
                .errorMessage(apiServeErrorrException.getOriginalMessage())
                .build();

        return response.writeWith(
                Mono.just(response
                        .bufferFactory().wrap(new ObjectMapper()
                                .writeValueAsBytes(apiErrorResponse)))
        );
    }

    @ExceptionHandler(Exception.class)
    public Mono<ServerResponse> handleGeneralException(Exception ex, ServerWebExchange serverWebExchange) {
        log.error("An unexpected Exception occurred {}", ex.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Unexpected Internal Server Error.");
        body.put("message",ex.getMessage());
        body.put("pathError", serverWebExchange.getRequest().getPath());
        body.put("stackTrace", ex.getStackTrace());

        return ServerResponse
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue(body);
    }

}
