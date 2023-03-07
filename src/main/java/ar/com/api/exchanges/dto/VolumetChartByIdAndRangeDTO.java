package ar.com.api.exchanges.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VolumetChartByIdAndRangeDTO implements IFilterDTO {

 private String id;
 private String fromDate;
 private String toDate;

 @Override
 public String getUrlFilterString() {

  StringBuilder urlBuilder = new StringBuilder();
  urlBuilder.append("?from=").append(fromDate)
            .append("&to=").append(toDate);
  
  return urlBuilder.toString();
 }
 
}
