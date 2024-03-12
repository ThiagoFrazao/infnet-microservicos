package br.edu.infnet.authorization.exception;

public class DatabaseConnectionException extends RuntimeException {

    public DatabaseConnectionException(String databaseName, Throwable e) {
        super("Falha %s ao conectar a Base de Dados %s.".formatted(e.getClass().getSimpleName(), databaseName), e);
    }

}
