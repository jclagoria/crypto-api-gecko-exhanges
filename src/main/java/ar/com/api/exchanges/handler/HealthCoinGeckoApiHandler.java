package ar.com.api.exchanges.handler;

import ar.com.api.exchanges.enums.ErrorTypeEnum;
import ar.com.api.exchanges.exception.ApiClientErrorException;
import ar.com.api.exchanges.model.Ping;
import ar.com.api.exchanges.services.CoinGeckoServiceStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class HealthCoinGeckoApiHandler {

    private CoinGeckoServiceStatus serviceStatus;

    /**
     * @param serverRequest
     * @return
     */
    public Mono<ServerResponse> getStatusServiceCoinGecko(ServerRequest serverRequest) {
        log.info("In getStatusServiceCoinGecko, handling request {}", serverRequest.path());

        return serviceStatus.getStatusCoinGeckoService()
                .flatMap(ping -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ping))
                .doOnSubscribe(subscription -> log.info("Retrieving status of Gecko Service"))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(error -> Mono
                        .error(new ApiClientErrorException(
                                "An expected error occurred in getStatusServiceCoinGecko",
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                ErrorTypeEnum.API_SERVER_ERROR
                        )));
    }
}
