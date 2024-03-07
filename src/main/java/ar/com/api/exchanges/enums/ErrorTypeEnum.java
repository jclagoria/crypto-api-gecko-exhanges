package ar.com.api.exchanges.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorTypeEnum {

    API_CLIENT_ERROR("Api Client Error"),
    API_SERVER_ERROR("Api Server Error"),
    GECKO_CLIENT__ERROR("Gecko Client Error"),
    GECKO_SERVER_ERROR("Gecko Server Error");

    private String description;
}