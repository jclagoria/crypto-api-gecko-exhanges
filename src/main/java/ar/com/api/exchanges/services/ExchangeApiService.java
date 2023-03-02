package ar.com.api.exchanges.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ar.com.api.exchanges.dto.ExchangeDTO;
import ar.com.api.exchanges.model.Exchange;
import ar.com.api.exchanges.model.ExchangeBase;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class ExchangeApiService {

 @Value("${api.exchangeList}")
 private String URL_EXCHANGE_GECKO_SERVICE_API;
 
 @Value("${api.exchangeListMarket}")
 private String URL_EXCHANGE_LIST_GECKO_SERVICE_API;
 
 private WebClient webClient;

 public ExchangeApiService(WebClient wClient) {
  this.webClient = wClient;
 }

 public Flux<Exchange> getAllExchanges(ExchangeDTO filterDTO) { 

  log.info("In service getAllExchanges " + URL_EXCHANGE_GECKO_SERVICE_API + filterDTO.getUrlFilterString());

  return webClient 
            .get()
            .uri(URL_EXCHANGE_GECKO_SERVICE_API + filterDTO.getUrlFilterString())
            .retrieve()
            .bodyToFlux(Exchange.class)
            .doOnError(throwable -> log.error("The service is unavailable!", throwable))
            .onErrorComplete();
 }

 public Flux<ExchangeBase> getAllSupportedMarkets() {

  log.info("In service getAllSupportedMarkets -> " 
               + URL_EXCHANGE_GECKO_SERVICE_API 
               + URL_EXCHANGE_LIST_GECKO_SERVICE_API);

  return webClient
             .get()
             .uri(URL_EXCHANGE_GECKO_SERVICE_API + URL_EXCHANGE_LIST_GECKO_SERVICE_API)
             .retrieve()
             .bodyToFlux(ExchangeBase.class)
             .doOnError(throwable -> log.error("The service is unavailable!", throwable))
            .onErrorComplete();
 }
 
}
