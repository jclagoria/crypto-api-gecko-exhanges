package ar.com.api.exchanges.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeById implements Serializable {

    @JsonProperty("name")
    private String name;

    @JsonProperty("year_established")
    private long yearEstablished;

    @JsonProperty("description")
    private String description;

    @JsonProperty("url")
    private String url;

    @JsonProperty("image")
    private String image;

    @JsonProperty("facebook_url")
    private String facebookUrl;

    @JsonProperty("reddit_url")
    private String redditUrl;

    @JsonProperty("telegram_url")
    private String telegramUrl;

    @JsonProperty("slack_url")
    private String slackUrl;

    @JsonProperty("other_url_1")
    private String otherUrl1;

    @JsonProperty("other_url_2")
    private String otherUrl2;

    @JsonProperty("twitter_handle")
    private String twitterHandle;

    @JsonProperty("has_trading_incentive")
    private boolean hasTradingIncentive;

    @JsonProperty("centralized")
    private boolean centralized;

    @JsonProperty("public_notice")
    private String publicNotice;

    @JsonProperty("alert_notice")
    private String alertNotice;

    @JsonProperty("trust_score")
    private long trustScore;

    @JsonProperty("trust_score_rank")
    private long trustScoreRank;

    @JsonProperty("trade_volume_24h_btc")
    private double tradeVolume24hBtc;

    @JsonProperty("trade_volume_24h_btc_normalized")
    private double tradeVolume24hBtcNormalized;

    @JsonProperty("tickers")
    private List<Ticker> tickers;

    @JsonProperty("status_updates")
    private List<StatusUpdate> statusUpdates;

}