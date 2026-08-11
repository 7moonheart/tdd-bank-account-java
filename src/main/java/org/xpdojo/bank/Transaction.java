package org.xpdojo.bank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// 封装交易信息
public class Transaction {
    private final LocalDateTime timestamp;
    private final String type;
    private final int amount;
    private final int balanceAfter;

    public Transaction(String type, int amount, int balanceAfter) {
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    public String format() {
        String formattedTime = timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String sign = amount >= 0 ? "+" : "";
        return String.format("%s  %s    %s%d    %d",
                formattedTime, type, sign, amount, balanceAfter);
    }
}