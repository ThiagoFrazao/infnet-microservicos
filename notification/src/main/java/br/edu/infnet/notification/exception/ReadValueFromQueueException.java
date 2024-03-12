package br.edu.infnet.notification.exception;

public class ReadValueFromQueueException extends RuntimeException {

    public ReadValueFromQueueException(String msg, Throwable e) {
        super(msg, e);
    }

}
