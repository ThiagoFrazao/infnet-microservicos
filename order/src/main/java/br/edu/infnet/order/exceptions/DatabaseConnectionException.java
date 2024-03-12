package br.edu.infnet.order.exceptions;

public class DatabaseConnectionException extends RuntimeException {

    public DatabaseConnectionException(String databaseName, Throwable e) {
        super("Falha %s ao conectar a Base de Dados %s.".formatted(e.getClass().getSimpleName(), databaseName), e);
    }

}
