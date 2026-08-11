package org.xpdojo.bank;

public class DefaultNotificationService implements NotificationService {
    @Override
    public void notify(String message) {

    }

    @Override
    public boolean send(String message) {
        // 这里可以打印日志，或者什么都不做
        System.out.println("通知发送: " + message);
        return true;
    }
}