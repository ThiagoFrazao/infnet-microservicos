package com.store.notification.component;

public interface RabbitMqComponent {

    void handleMessage(String message);

}
