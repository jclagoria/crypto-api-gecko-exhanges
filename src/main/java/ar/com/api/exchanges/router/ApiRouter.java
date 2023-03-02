package ar.com.api.exchanges.router;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import ar.com.api.exchanges.handler.ExchangesApiHandler;

@Configuration
public class ApiRouter {
 
 @Value("${coins.baseURL}")
 private String URL_SERVICE_API;

 @Value("${coins.healthAPI}")
 private String URL_HEALTH_GECKO_API;

 @Value("${coins.exchangeList}")
 private String URL_EXCHANGE_GECKO_API;
 
 @Value("${coins.exchangeListMarket}")
 private String URL_EXCHANGE_LIST_MARKET_API; 
 
 @Value("${coins.exchangeById}")
 private String URL_EXCHANGE_VOLUMN_DATA_BY_ID_API;

 @Bean
 public RouterFunction<ServerResponse> route(ExchangesApiHandler handler) {

  return RouterFunctions
            .route()
            .GET(URL_SERVICE_API + URL_HEALTH_GECKO_API, 
                        handler::getStatusServiceCoinGecko)
            .GET(URL_SERVICE_API + URL_EXCHANGE_GECKO_API, 
                        RequestPredicates.accept(MediaType.APPLICATION_JSON),
                        handler::getAllExchangesCoinGecko)
            .GET(URL_SERVICE_API + URL_EXCHANGE_GECKO_API + URL_EXCHANGE_LIST_MARKET_API,
                        handler::getAllExchangeMarketData)
            .GET(URL_SERVICE_API + URL_EXCHANGE_GECKO_API + URL_EXCHANGE_VOLUMN_DATA_BY_ID_API,
                        handler::getExchangeVolumenDataById)                        
            .build();

 }
 
}