package ar.com.api.exchanges.router;

import ar.com.api.exchanges.configuration.ApiServiceConfig;
import ar.com.api.exchanges.handler.ExchangesApiHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ExchangeApiRouter {

    private final ApiServiceConfig apiServiceConfig;

    public ExchangeApiRouter(ApiServiceConfig serviceConfig) {
        this.apiServiceConfig = serviceConfig;
    }

    @Bean
    public RouterFunction<ServerResponse> routeExchange(ExchangesApiHandler handler) {

        return RouterFunctions
                .route()
                .GET(apiServiceConfig.getBaseURL() + apiServiceConfig.getExchangeList(),
                        RequestPredicates.accept(MediaType.APPLICATION_JSON),
                        handler::getAllExchangesCoinGecko)
                .GET(apiServiceConfig.getBaseURL() + apiServiceConfig.getExchangeList()
                                + apiServiceConfig.getExchangeListMarket(),
                        handler::getAllExchangeMarketData)
                .GET(apiServiceConfig.getBaseURL() + apiServiceConfig.getExchangeList()
                                + apiServiceConfig.getExchangeById(),
                        handler::getExchangeVolumenDataById)
                .GET(apiServiceConfig.getBaseURL() + apiServiceConfig.getExchangeList()
                                + apiServiceConfig.getExchangeTickerById(),
                        RequestPredicates.accept(MediaType.APPLICATION_JSON),
                        handler::getTickerExchangeById)
                .GET(apiServiceConfig.getBaseURL() + apiServiceConfig.getExchangeList()
                                + apiServiceConfig.getExchangeVolumeChart(),
                        RequestPredicates.accept(MediaType.APPLICATION_JSON),
                        handler::getVolumeChartById)
                .build();
    }

}