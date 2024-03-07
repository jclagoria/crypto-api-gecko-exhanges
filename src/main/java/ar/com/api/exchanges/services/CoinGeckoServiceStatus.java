package ar.com.api.exchanges.services;

import ar.com.api.exchanges.configuration.ExternalServerConfig;
import ar.com.api.exchanges.configuration.HttpServiceCall;
import ar.com.api.exchanges.model.Ping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CoinGeckoServiceStatus {

    private final HttpServiceCall httpServiceCall;
    private final ExternalServerConfig externalServerConfig;

    public CoinGeckoServiceStatus(HttpServiceCall serviceCall, ExternalServerConfig serverConfig) {
        this.httpServiceCall = serviceCall;
        this.externalServerConfig = serverConfig;
    }

    public Mono<Ping> getStatusCoinGeckoService() {

        log.info("Calling EndPint on GeckoAPI: {}", externalServerConfig.getPing());

        return httpServiceCall.getMonoObject(externalServerConfig.getPing(), Ping.class);
    }

}
