package ar.com.api.exchanges.handler;

import ar.com.api.exchanges.dto.ExchangeDTO;
import ar.com.api.exchanges.dto.ExchangeVolumenDTO;
import ar.com.api.exchanges.dto.TickersByIdDTO;
import ar.com.api.exchanges.dto.VolumeChartByIdDTO;
import ar.com.api.exchanges.model.*;
import ar.com.api.exchanges.services.CoinGeckoServiceStatus;
import ar.com.api.exchanges.services.ExchangeApiService;
import ar.com.api.exchanges.utils.StringToInteger;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class ExchangesApiHandler {

    private ExchangeApiService serviceExchange;

    /**
     * @param sRequest
     * @return
     */
    public Mono<ServerResponse> getAllExchangesCoinGecko(ServerRequest sRequest) {

        log.info("In getAllExchangesCoinGecko");

        Optional<Integer> optPerPage = Optional.empty();

        if (sRequest.queryParam("perPage").isPresent()) {
            optPerPage = Optional
                    .of(sRequest.queryParam("perPage")
                            .get()
                            .transform(StringToInteger.INSTANCE));
        }

        ExchangeDTO filterDto = ExchangeDTO
                .builder()
                .perPage(optPerPage)
                .page(sRequest.queryParam("page"))
                .build();

        return ServerResponse
                .ok()
                .body(
                        serviceExchange.getAllExchanges(filterDto),
                        Exchange.class);
    }

    /**
     * @param sRequest
     * @return
     */
    public Mono<ServerResponse> getAllExchangeMarketData(ServerRequest sRequest) {

        log.info("In getAllExchangeMarketData");

        return ServerResponse
                .ok()
                .body(
                        serviceExchange.getAllSupportedMarkets(),
                        ExchangeBase.class);
    }

    /**
     * @param sRequest
     * @return
     */
    public Mono<ServerResponse> getExchangeVolumenDataById(ServerRequest sRequest) {

        log.info("In getExchangeVolumenDataById");

        ExchangeVolumenDTO filterDTO = ExchangeVolumenDTO
                .builder()
                .id(sRequest.pathVariable("idMarket"))
                .build();

        return ServerResponse
                .ok()
                .body(
                        serviceExchange.getExchangeVolumenById(filterDTO),
                        ExchangeById.class);
    }

    public Mono<ServerResponse> getTickerExchangeById(ServerRequest sRequest) {

        log.info("In getTickerExchangeById");

        Optional<Integer> optPerPage = Optional.empty();

        if (sRequest.queryParam("page").isPresent()) {
            optPerPage = Optional
                    .of(sRequest.queryParam("page")
                            .get()
                            .transform(StringToInteger.INSTANCE));
        }

        TickersByIdDTO filterDTO = TickersByIdDTO
                .builder()
                .id(sRequest.pathVariable("idMarket"))
                .coinIds(sRequest.queryParam("coinIds"))
                .includeExchangeLogo(sRequest.queryParam("includeExchangeLogo"))
                .page(optPerPage)
                .depth(sRequest.queryParam("depth"))
                .order(sRequest.queryParam("order"))
                .build();

        return ServerResponse
                .ok()
                .body(serviceExchange.getTicketExchangeById(filterDTO),
                        TickersById.class);
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