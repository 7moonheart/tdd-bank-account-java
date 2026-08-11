package org.xpdojo.bank;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AccountNotificationTest {

    @Mock
    NotificationService notificationService; // 创建 Mock 对象

    @InjectMocks
    Account account; // 将 Mock 注入到 Account 中
    // 此时account内部的notificationService指向的是这个虚拟服务，而不是真正的服务

    @Test
    void shouldSendNotificationWhenDepositSuccess(){
        // 设置：当 send() 被调用时，返回 true
        when(notificationService.send(anyString())).thenReturn(true); // 模拟返回值
        // 执行存款操作
        account.deposit(new Money(100));
        // 验证：通知服务被调用了一次，且参数为 ”存款成功，当前余额：100“
        verify(notificationService, times(1)).send("存款成功，当前余额：100");
        // 验证：余额也正确（额外检查，确保存款逻辑没被影响）
        assertThat(account.getBalance().getValue()).isEqualTo(100);
    }

    @Test
    void shouldDepositEvenWhenNotificationFails() {
        // 设置：当 send() 被调用时，返回 false
        when(notificationService.send(anyString())).thenReturn(false);
        // 执行
        account.deposit(new Money(100));
        // 验证：通知被调用了，但返回了 false
        verify(notificationService, times(1)).send("存款成功，当前余额：100");
        // 验证：存款本身仍然成功了（这是核心业务逻辑，不应该受通知失败影响）
        assertThat(account.getBalance().getValue()).isEqualTo(100);
    }

    @Test
    void shouldNotSendNotificationWhenDepositFails(){
        // 注意：这里不需要 stub，因为 deposit 会抛出异常，不会调用 send()
        // 执行 + 验证：尝试存款负数，应该抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            account.deposit(new Money(-5));
        });
        // 验证通知服务没有被调用
//        verify(notificationService, never()).notify(anyString());
        verify(notificationService, never()).send(anyString());
    }

    @Test
    void shouldDepositSuccessfullyEvenWhenNotificationFailsWithException() {
        // 1. 设置：当 send() 被调用时，抛出异常
        when(notificationService.send(anyString())).thenThrow(new RuntimeException("短信服务异常"));
        // 2. 执行：调用存款
        account.deposit(new Money(100));
        // 3. 验证：
        // 3.1 存款业务本身成功
        assertThat(account.getBalance().getValue()).isEqualTo(100);
        // 3.2 通知确实被调用了（但抛了异常）
        verify(notificationService, times(1)).send("存款成功，当前余额：100");
    }

    @Test
    void shouldSendNotificationWithExactMessageWhenDepositSuccess(){
        // 执行：存款100元
        account.deposit(new Money(100));
        // 精确匹配：必须完全等于 "存款成功，当前余额：100"
        verify(notificationService, times(1)).send(eq("存款成功，当前余额：100"));
        // 自定义匹配器：只要消息里包含“存款成功”，就算匹配
        verify(notificationService, times(1)).send(argThat(msg -> msg.contains("存款成功")));
    }
}
