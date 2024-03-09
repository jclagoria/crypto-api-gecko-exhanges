package ar.com.api.exchanges.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
@Builder
public class ExchangeDTO implements IFilterDTO {

    private Optional<Integer> perPage;
    private Optional<String> page;


    @Override
    public String getUrlFilterString() {

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("?per_page=").append(perPage.orElse(100));

        this.getPage().ifPresent(pageNum -> urlBuilder.
                append("&page=").append(pageNum));

        return urlBuilder.toString();
    }

}
