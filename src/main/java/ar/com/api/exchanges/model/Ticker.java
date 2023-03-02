package ar.com.api.exchanges.model;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticker implements Serializable {
 
 @JsonProperty("base")
 private String base;

 @JsonProperty("target")
 private String target;

 @JsonProperty("market")
 private Market market;

 @JsonProperty("last")
 private double last;

 @JsonProperty("volume")
 private double volume;

 @JsonProperty("converted_last")
 private Map<String, Double> convertedLast;

 @JsonProperty("converted_volume")
 private Map<String, Double> convertedVolume;

 @JsonProperty("trust_score")
 private String trustScore;

 @JsonProperty("bid_ask_spread_percentage")
 private double bidAskSpreadPercentage;

 @JsonProperty("timestamp")
 private String timestamp;

 @JsonProperty("last_traded_at")
 private String lastTradedAt;

 @JsonProperty("last_fetch_at")
 private String lastFetchAt;

 @JsonProperty("is_anomaly")
 private boolean isAnomaly;

 @JsonProperty("is_stale")
 private boolean isStale;

 @JsonProperty("trade_url")
 private String tradeUrl;

 @JsonProperty("token_info_url")
 private String tokenInfoUrl;

 @JsonProperty("coin_id")
 private String coinId;

 @JsonProperty("target_coin_id")
 private String targetCoinId;

}
