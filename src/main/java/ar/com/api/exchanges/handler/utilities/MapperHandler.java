package ar.com.api.exchanges.handler.utilities;

import ar.com.api.exchanges.dto.ExchangeDTO;
import ar.com.api.exchanges.utils.StringToInteger;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class MapperHandler {

    public static ExchangeDTO createExchangeDTOFromRequest(ServerRequest sRequest) {
        Optional<Integer> optPerPage = sRequest.queryParam("perPage").map(Integer::valueOf);

        return ExchangeDTO
                .builder()
                .perPage(optPerPage)
                .page(sRequest.queryParam("page"))
                .build();
    }
}
