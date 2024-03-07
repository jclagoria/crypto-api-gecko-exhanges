package ar.com.api.exchanges.exception;

import ar.com.api.exchanges.enums.ErrorTypeEnum;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiClientErrorException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final ErrorTypeEnum errorTypeEnum;

    public ApiClientErrorException(String message, HttpStatus status, ErrorTypeEnum typeEnum) {
        super(message);
        this.httpStatus = status;
        this.errorTypeEnum = typeEnum;
    }
}
