package ar.com.api.exchanges.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Image implements Serializable {

    @JsonProperty("thumb")
    private String thumb;

    @JsonProperty("small")
    private String small;

    @JsonProperty("large")
    private String large;

}
