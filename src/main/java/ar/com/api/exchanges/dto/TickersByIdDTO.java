package ar.com.api.exchanges.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
@Builder
public class TickersByIdDTO implements IFilterDTO {

    private String id;
    private Optional<String> coinIds;
    private Optional<String> includeExchangeLogo;
    private Optional<Integer> page;
    private Optional<String> depth;
    private Optional<String> order;

    @Override
    public String getUrlFilterString() {

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("?order=")
                .append(order.orElse("trust_score_desc"));

        if (coinIds.isPresent())
            urlBuilder.append("&coin_ids=")
                    .append(coinIds.get());

        if (includeExchangeLogo.isPresent())
            urlBuilder.append("&include_exchange_logo=")
                    .append(includeExchangeLogo.get());

        if (page.isPresent())
            urlBuilder.append("&page=")
                    .append(page.get());

        if (depth.isPresent())
            urlBuilder.append("&depth=")
                    .append(depth.get());

        return urlBuilder.toString();
    }

}
