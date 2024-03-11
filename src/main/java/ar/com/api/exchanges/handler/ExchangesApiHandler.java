package ar.com.api.exchanges.handler;

import ar.com.api.exchanges.dto.ExchangeVolumeDTO;
import ar.com.api.exchanges.dto.TickersByIdDTO;
import ar.com.api.exchanges.dto.VolumeChartByIdDTO;
import ar.com.api.exchanges.enums.ErrorTypeEnum;
import ar.com.api.exchanges.exception.ApiClientErrorException;
import ar.com.api.exchanges.handler.utilities.MapperHandler;
import ar.com.api.exchanges.model.ExchangeById;
import ar.com.api.exchanges.model.TickersById;
import ar.com.api.exchanges.services.ExchangeApiService;
import ar.com.api.exchanges.utils.StringToInteger;
import ar.com.api.exchanges.validators.ValidatorOfCTOComponent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class ExchangesApiHandler {

    private final ExchangeApiService serviceExchange;
    private final ValidatorOfCTOComponent validatorComponent;

    /**
     * @param sRequest
     * @return
     */
    public Mono<ServerResponse> getAllExchangesCoinGecko(ServerRequest sRequest) {
        log.info("Fetching List of Exchanges from CoinGecko API");

        return Mono.just(sRequest)
                .map(MapperHandler::createExchangeDTOFromRequest)
                .flatMapMany(serviceExchange::getAllExchanges)
                .collectList()
                .flatMap(exchanges -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(exchanges)
                ).switchIfEmpty(ServerResponse.notFound().build())
                .doOnSubscribe(subscription -> log.info("Retrieving list of Exchanges"))
                .onErrorResume(error -> Mono
                        .error(new ApiClientErrorException(
                                "An unexpected error occurred in getAllExchangesCoinGecko",
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                ErrorTypeEnum.API_SERVER_ERROR
                        )));
    }

    /**
     * @param sRequest
     * @return
     */
    public Mono<ServerResponse> getAllExchangeMarketData(ServerRequest sRequest) {
        log.info("Fetching List of Market Exchanges from CoinGecko API");

        return serviceExchange.getAllSupportedMarkets()
                .collectList()
                .flatMap(exchangesList -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(exchangesList))
                .switchIfEmpty(ServerResponse.notFound().build())
                .doOnSubscribe(subscription -> log.info("Retrieving list of Market Exchanges"))
                .onErrorResume(error -> Mono.
                        error(new ApiClientErrorException("An unexpected error occurred in getAllExchangeMarketData",
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                ErrorTypeEnum.API_SERVER_ERROR))
                );
    }

    /**
     * @param sRequest
     * @return
     */
    public Mono<ServerResponse> getExchangeVolumeDataById(ServerRequest sRequest) {
        log.info("Fetching Exchange Volume Data by Market ID from CoinGecko API");

        return Mono.just(sRequest)
                .flatMap(MapperHandler::createExchangeVolumeDTOFromRequest)
                .flatMap(validatorComponent::validation)
                .flatMap(serviceExchange::getExchangeVolumesById)
                .flatMap(exchangeVolume -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(exchangeVolume))
                .switchIfEmpty(ServerResponse.notFound().build())
                .doOnSubscribe(subscription -> log.info("Retrieving Exchange Info by Market ID"))
                .onErrorResume(error -> Mono.error(
                        new ApiClientErrorException("An unexpected error occurred in getExchangeVolumeDataById",
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                ErrorTypeEnum.API_SERVER_ERROR)
                ));
    }

    public Mono<ServerResponse> getTickerExchangeById(ServerRequest sRequest) {
        log.info("Fetching Ticker Exchange by Market ID from CoinGecko API");

        return Mono.just(sRequest)
                .flatMap(MapperHandler::createTickersByIdDTOFromRequest)
                .flatMap(validatorComponent::validation)
                .flatMap(serviceExchange::getTicketExchangeById)
                .flatMap(tickerList -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(tickerList))
                .switchIfEmpty(ServerResponse.notFound().build())
                .doOnSubscribe(subscription -> log.info("Retrieving Ticker by Market ID"))
                .onErrorResume(error -> Mono.error(
                        new ApiClientErrorException("An unexpected error occurred in getTickerExchangeById",
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                ErrorTypeEnum.API_SERVER_ERROR)
                ));
    }

    public Mono<ServerResponse> getVolumeChartById(ServerRequest sRequest) {

        log.info("In getVolumeChartById");

        VolumeChartByIdDTO filterDTO = VolumeChartByIdDTO
                .builder()
                .id(sRequest.pathVariable("idMarket"))
                .days(Integer.valueOf(sRequest.queryParam("days").get()))
                .build();

        return ServerResponse
                .ok()
                .body(
                        serviceExchange.getVolumeChartById(filterDTO),
                        String.class);
    }

}