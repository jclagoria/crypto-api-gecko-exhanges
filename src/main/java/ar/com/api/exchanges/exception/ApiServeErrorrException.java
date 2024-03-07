package ar.com.api.exchanges.exception;

import ar.com.api.exchanges.enums.ErrorTypeEnum;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiServeErrorrException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String originalMessage;
    private final ErrorTypeEnum errorTypeEnum;

    public ApiServeErrorrException(String message, String originalMessage,
                                   ErrorTypeEnum typeEnum, HttpStatus status) {
        super(message);
        this.originalMessage = originalMessage;
        this.errorTypeEnum = typeEnum;
        this.httpStatus = status;
    }

}
