package br.edu.infnet.authorization.exception;

import br.edu.infnet.authorization.dto.error.AuthorizationError;
import br.edu.infnet.authorization.dto.error.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AuthorizationControllerAdvice {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AuthorizationError businessExceptionHandler(BusinessException exception) {
        return new AuthorizationError(exception.getMessage(), ErrorType.RECUPERAVEL);
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AuthorizationError genericExceptionHandler(Exception exception) {
        return new AuthorizationError(exception.getMessage(), ErrorType.NAO_RECUPERAVAL);
    }

}