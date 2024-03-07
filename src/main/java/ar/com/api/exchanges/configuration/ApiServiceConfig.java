package ar.com.api.exchanges.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "coins")
@Getter
@Setter
public class ApiServiceConfig {
    
    private String baseURL;
    private String healthAPI;
    private String exchangeList;
    private String exchangeListMarket;
    private String exchangeById;
    private String exchangeTickerById;
    private String exchangeVolumeChart;
    
}
