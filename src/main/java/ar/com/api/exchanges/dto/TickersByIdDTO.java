package ar.com.api.exchanges.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
@Builder
public class TickersByIdDTO implements IFilterDTO {

    @NotBlank(message = "Exchange ID cannot be blanc.")
    @NotEmpty(message = "Exchange ID cannot be empty.")
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

        this.coinIds.ifPresent( coinId -> urlBuilder.append("&coin_ids=").append(coinId));
        this.includeExchangeLogo.ifPresent(includeLogo -> urlBuilder
                .append("&include_exchange_logo=").append(includeLogo));
        this.page.ifPresent(actualPage -> urlBuilder.append("&page=").append(actualPage));
        this.depth.ifPresent(depthValue -> urlBuilder.append("&depth=").append(depthValue));

        return urlBuilder.toString();
    }

}
