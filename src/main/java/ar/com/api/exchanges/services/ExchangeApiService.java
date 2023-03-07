package ar.com.api.exchanges.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ar.com.api.exchanges.dto.ExchangeDTO;
import ar.com.api.exchanges.dto.ExchangeVolumenDTO;
import ar.com.api.exchanges.dto.TickersByIdDTO;
import ar.com.api.exchanges.dto.VolumeChartByIdDTO;
import ar.com.api.exchanges.dto.VolumetChartByIdAndRangeDTO;
import ar.com.api.exchanges.model.Exchange;
import ar.com.api.exchanges.model.ExchangeBase;
import ar.com.api.exchanges.model.ExchangeById;
import ar.com.api.exchanges.model.TickersById;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ExchangeApiService {

 @Value("${api.exchangeList}")
 private String URL_EXCHANGE_GECKO_API;
 
 @Value("${api.exchangeListMarket}")
 private String URL_EXCHANGE_LIST_GECKO_API;

 @Value("${api.exchangeById}")
 private String URL_EXCHANGE_BY_ID_GECKO_API;

 @Value("${api.exchangeTickerById}")
 private String URL_TICKER_EXCHANGE_BY_ID_GECKO_API;

 @Value("${api.exchangeVolumeChart}")
 private String URL_VOLUME_CHART_BY_ID_GECKO_API;

 @Value("${api.exchangeVolumeChartRange}")
 private String URL_VOLUME_CHART_BY_RANGE_GECKO_API;
 
 private WebClient webClient;

 public ExchangeApiService(WebClient wClient) {
  this.webClient = wClient;
 }

 /**
  * 
  * @param filterDTO
  * @return
  */
 public Flux<Exchange> getAllExchanges(ExchangeDTO filterDTO) { 

  log.info("In service getAllExchanges " + URL_EXCHANGE_GECKO_API + filterDTO.getUrlFilterString());

  return webClient 
            .get()
            .uri(URL_EXCHANGE_GECKO_API + filterDTO.getUrlFilterString())
            .retrieve()
            .bodyToFlux(Exchange.class)
            .doOnError(throwable -> log.error("The service is unavailable!", throwable))
            .onErrorComplete();
 }

 /**
  * 
  * @return
  */
 public Flux<ExchangeBase> getAllSupportedMarkets() {

  log.info("In service getAllSupportedMarkets -> " 
               + URL_EXCHANGE_GECKO_API 
               + URL_EXCHANGE_LIST_GECKO_API);

  return webClient
             .get()
             .uri(URL_EXCHANGE_GECKO_API + URL_EXCHANGE_LIST_GECKO_API)
             .retrieve()
             .bodyToFlux(ExchangeBase.class)
             .doOnError(throwable -> log.error("The service is unavailable!", throwable))
            .onErrorComplete();
 }

 /**
  * 
  * @param filterDTO
  * @return
  */
 public Mono<ExchangeById> getExchangeVolumenById(ExchangeVolumenDTO filterDTO) {

  log.info("In service getExchangeVolumenById -> " 
              + URL_EXCHANGE_GECKO_API 
              + URL_EXCHANGE_BY_ID_GECKO_API);
  
  String idMarket = String.format(URL_EXCHANGE_BY_ID_GECKO_API, filterDTO.getId()); 

  return webClient
            .get()
            .uri(URL_EXCHANGE_GECKO_API + idMarket)
            .retrieve()
            .bodyToMono(ExchangeById.class)
            .doOnError(throwable -> log.error("The service is unavailable!", throwable))
            .onErrorComplete();
 }

 /**
  * 
  * @param filterDTO
  * @return
  */
 public Mono<TickersById> getTicketExchangeById(TickersByIdDTO filterDTO) {

  log.info("In service getTicketExchangeById -> " 
              + URL_EXCHANGE_GECKO_API 
              + URL_TICKER_EXCHANGE_BY_ID_GECKO_API);
  
  String urlFilter = String.format(URL_TICKER_EXCHANGE_BY_ID_GECKO_API, filterDTO.getId());  

  return webClient
           .get()
           .uri(URL_EXCHANGE_GECKO_API + urlFilter + filterDTO.getUrlFilterString())
           .retrieve()
           .bodyToMono(TickersById.class)
           .doOnError(throwable -> log.error("The service is unavailable!", throwable))
           .onErrorComplete();
 }

 public Flux<String> getVolumeChartById(VolumeChartByIdDTO filterDto) {

  log.info("In service getTicketExchangeById -> " 
              + URL_EXCHANGE_GECKO_API 
              + URL_VOLUME_CHART_BY_ID_GECKO_API);
         
  String urlApiGecko = String.format(URL_VOLUME_CHART_BY_ID_GECKO_API, filterDto.getId());  

  return webClient
            .get()
            .uri(URL_EXCHANGE_GECKO_API + urlApiGecko + filterDto.getUrlFilterString())
            .retrieve()
            .bodyToFlux(String.class)
            .doOnError(throwable -> log.error("The service is unavailable!", throwable))
            .onErrorComplete();
 }

 public Flux<String> getVolumeChartByIdAAndRangeDate(VolumetChartByIdAndRangeDTO filterDTO) {

  log.info("In service getTicketExchangeById -> " 
              + URL_EXCHANGE_GECKO_API 
              + URL_VOLUME_CHART_BY_RANGE_GECKO_API);
  
  String urlFilter = String.format(URL_VOLUME_CHART_BY_RANGE_GECKO_API, filterDTO.getId());

  log.info("Value -> "+URL_EXCHANGE_GECKO_API + urlFilter + filterDTO.getUrlFilterString());

  return webClient
             .get()
             .uri(URL_EXCHANGE_GECKO_API + urlFilter + filterDTO.getUrlFilterString())
             .retrieve()
             .bodyToFlux(String.class)
             .doOnError(throwable -> log.error("The service is unavailable!", throwable))
             .onErrorComplete();
 }
 
}
