package ar.com.api.exchanges.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExchangeVolumeDTO implements IFilterDTO {

    @NotBlank(message = "Exchange ID cannot be blanc.")
    @NotEmpty(message = "Exchange ID cannot be empty.")
    private String id;

    @Override
    public String getUrlFilterString() {
        return null;
    }


}
