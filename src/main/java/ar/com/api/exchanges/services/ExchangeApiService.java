package ar.com.api.exchanges.services;

import ar.com.api.exchanges.configuration.ExternalServerConfig;
import ar.com.api.exchanges.configuration.HttpServiceCall;
import ar.com.api.exchanges.dto.ExchangeDTO;
import ar.com.api.exchanges.dto.ExchangeVolumeDTO;
import ar.com.api.exchanges.dto.TickersByIdDTO;
import ar.com.api.exchanges.dto.VolumeChartByIdDTO;
import ar.com.api.exchanges.model.Exchange;
import ar.com.api.exchanges.model.ExchangeBase;
import ar.com.api.exchanges.model.ExchangeById;
import ar.com.api.exchanges.model.TickersById;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ExchangeApiService {

    private final HttpServiceCall httpServiceCall;
    private final ExternalServerConfig externalServerConfig;

    public ExchangeApiService(HttpServiceCall serviceCall, ExternalServerConfig serverConfig) {
        this.httpServiceCall = serviceCall;
        this.externalServerConfig = serverConfig;
    }

    /**
     * @param filterDTO
     * @return
     */
    public Flux<Exchange> getAllExchanges(ExchangeDTO filterDTO) {
        log.info("In service getAllExchanges {} ", externalServerConfig.getExchangeList()
                + filterDTO.getUrlFilterString());

        return httpServiceCall.getFluxObject(externalServerConfig.getExchangeList()
                + filterDTO.getUrlFilterString(), Exchange.class);
    }

    /**
     * @return
     */
    public Flux<ExchangeBase> getAllSupportedMarkets() {

        log.info("In service getAllSupportedMarkets {} ",
                externalServerConfig.getExchangeList() + externalServerConfig.getExchangeListMarket());

        return httpServiceCall.getFluxObject(externalServerConfig.getExchangeList()
                + externalServerConfig.getExchangeListMarket(), ExchangeBase.class);
    }

    /**
     * @param filterDTO
     * @return
     */
    public Mono<ExchangeById> getExchangeVolumesById(ExchangeVolumeDTO filterDTO) {
        log.info("In service getExchangeVolumesById {}",
                externalServerConfig.getExchangeList() + externalServerConfig.getExchangeById());

        String idMarket = String.format(externalServerConfig.getExchangeById(), filterDTO.getId());

        return httpServiceCall.getMonoObject(externalServerConfig.getExchangeList() + idMarket,
                ExchangeById.class);
    }

    /**
     * @param filterDTO
     * @return
     */
    public Mono<TickersById> getTicketExchangeById(TickersByIdDTO filterDTO) {
        log.info("In service getTicketExchangeById {} ",
                externalServerConfig.getExchangeList() + externalServerConfig.getExchangeTickerById());

        String urlFilter = String.format(externalServerConfig.getExchangeTickerById(), filterDTO.getId());

        return httpServiceCall.getMonoObject(externalServerConfig.getExchangeList() + urlFilter,
                TickersById.class);
    }

    public Flux<String> getVolumeChartById(VolumeChartByIdDTO filterDto) {

        log.info("In service getTicketExchangeById {}"
                ,externalServerConfig.getExchangeList() + externalServerConfig.getExchangeVolumeChart());

        String urlApiGecko = String.format(externalServerConfig.getExchangeVolumeChart(), filterDto.getId());

        return null;
    }

}
