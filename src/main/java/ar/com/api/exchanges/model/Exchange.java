package ar.com.api.exchanges.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Exchange implements Serializable {

 @JsonProperty("id")
 private String id;

 @JsonProperty("name")
 private String name;

 @JsonProperty("year_established")
 private long yearEstablished;

 @JsonProperty("country")
 private String country;

 @JsonProperty("description")
 private String description;

 @JsonProperty("url")
 private String url;

 @JsonProperty("image")
 private String image;

 @JsonProperty("has_trading_incentive")
 private boolean hasTradingIncentive;

 @JsonProperty("trust_score")
 private long trustScore;

 @JsonProperty("trust_score_rank")
 private long trustScoreRank;

 @JsonProperty("trade_volume_24h_btc")
 private double tradeVolume24hBtc;

 @JsonProperty("trade_volume_24h_btc_normalized")
 private double tradeVolume24hBtcNormalized;

} 
