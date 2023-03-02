package ar.com.api.exchanges.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {
 
 @JsonProperty("type")
 private String type;

 @JsonProperty("id")
 private String id;

 @JsonProperty("name")
 private String name;

 @JsonProperty("image")
 private Image image;

}
