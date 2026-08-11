package org.xpdojo.bank;

public interface NotificationService {
    void notify(String message);
    boolean send(String message);
}