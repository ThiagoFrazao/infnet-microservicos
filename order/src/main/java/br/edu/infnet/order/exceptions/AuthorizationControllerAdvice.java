package br.edu.infnet.order.exceptions;

import br.edu.infnet.order.dto.error.OrderError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AuthorizationControllerAdvice {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public OrderError businessExceptionHandler(BusinessException exception) {
        return new OrderError(exception.getMessage());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public OrderError genericExceptionHandler(Exception exception) {
        return new OrderError(exception.getMessage());
    }

}