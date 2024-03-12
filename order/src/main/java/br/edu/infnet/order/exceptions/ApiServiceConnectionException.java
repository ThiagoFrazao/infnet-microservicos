package br.edu.infnet.order.exceptions;

public class ApiServiceConnectionException extends RuntimeException {

    public ApiServiceConnectionException(String serviceName, String apiPath) {
        super("Falha ao conectar ao Service %s no endpoint '%s'".formatted(serviceName, apiPath));
    }

}