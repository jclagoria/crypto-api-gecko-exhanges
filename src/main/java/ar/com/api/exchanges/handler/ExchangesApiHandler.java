package ar.com.api.exchanges.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import ar.com.api.exchanges.model.Ping;
import ar.com.api.exchanges.services.CoinGeckoServiceStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class ExchangesApiHandler {
 
 private CoinGeckoServiceStatus serviceStatus;

 public Mono<ServerResponse> getStatusServiceCoinGecko(ServerRequest serverRequest) {

  log.info("In getStatusServiceCoinGecko");

  return ServerResponse
                .ok()
                .body(
                     serviceStatus.getStatusCoinGeckoService(), 
                     Ping.class);
 }

}
