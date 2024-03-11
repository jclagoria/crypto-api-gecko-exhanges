package ar.com.api.exchanges.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VolumeChartByIdDTO implements IFilterDTO {

    @NotBlank(message = "Exchange ID cannot be blanc.")
    @NotEmpty(message = "Exchange ID cannot be empty.")
    private String id;
    @NotBlank(message = "Days cannot be blanc.")
    @NotEmpty(message = "Days cannot be empty.")
    private String days;

    @Override
    public String getUrlFilterString() {

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("?days=").append(days);

        return urlBuilder.toString();
    }

}
