package br.edu.infnet.authorization.exception;

public class QueueServiceConnectionException extends RuntimeException {

    public QueueServiceConnectionException(String queueName, Throwable e) {
        super("Falha %s ao conectar na Fila %s".formatted(e.getClass().getSimpleName(), queueName), e);
    }

}