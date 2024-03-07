package ar.com.api.exchanges.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeBase implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

}
