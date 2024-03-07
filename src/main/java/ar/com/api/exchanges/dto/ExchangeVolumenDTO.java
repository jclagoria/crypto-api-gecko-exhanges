package ar.com.api.exchanges.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExchangeVolumenDTO implements IFilterDTO {

    private String id;

    @Override
    public String getUrlFilterString() {
        return null;
    }


}
