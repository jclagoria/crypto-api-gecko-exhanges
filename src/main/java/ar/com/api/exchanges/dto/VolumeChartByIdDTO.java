package ar.com.api.exchanges.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VolumeChartByIdDTO implements IFilterDTO {

    private String id;
    private int days;

    @Override
    public String getUrlFilterString() {

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("?days=").append(days);

        return urlBuilder.toString();
    }

}
