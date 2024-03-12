package br.edu.infnet.products.exception;

import br.edu.infnet.products.dto.ProductErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ProductControllerAdvice {


    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProductErrorResponseDto businessExceptionHandler(BusinessException exception) {
        return new ProductErrorResponseDto(exception.getMessage(), exception.getDetalhes());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProductErrorResponseDto genericExceptionHandler(Exception exception) {
        return new ProductErrorResponseDto(exception.getMessage());
    }

}