package ar.com.api.exchanges.router;

import ar.com.api.exchanges.configuration.ApiServiceConfig;
import ar.com.api.exchanges.configuration.ExternalServerConfig;
import ar.com.api.exchanges.handler.HealthCoinGeckoApiHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@Slf4j
public class HealthApiRouter {

    private final ApiServiceConfig apiServiceConfig;

    public HealthApiRouter(ApiServiceConfig serverConfig) {
        this.apiServiceConfig = serverConfig;
    }

    @Bean
    public RouterFunction<ServerResponse> routeHealth(HealthCoinGeckoApiHandler handler) {

        return RouterFunctions
                .route()
                .GET(apiServiceConfig.getBaseURL() + apiServiceConfig.getHealthAPI(),
                        handler::getStatusServiceCoinGecko)
                .build();

    }
}
