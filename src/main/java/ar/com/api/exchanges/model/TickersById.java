package ar.com.api.exchanges.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TickersById implements Serializable {

 @JsonProperty("name")
 private String name; 

 @JsonProperty("tickers")
 private List<Ticker> ticker;

}
