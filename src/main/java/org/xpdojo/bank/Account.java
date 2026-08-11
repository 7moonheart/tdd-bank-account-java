package org.xpdojo.bank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private Money balance;
    private List<Transaction> transactions;
    private NotificationService notificationService;

    public Account() {
        this.balance = new Money(0);
        this.transactions = new ArrayList<>();  // 初始化交易列表
    }

    public Account(NotificationService notificationService) {
        this.balance = new Money(0);
        this.transactions = new ArrayList<>();
        this.notificationService = notificationService;
    }

    public Money getBalance() {
        return balance;
    }

//    public void deposit(Money amount) {
//        if (amount.getValue() <= 0) {
//            throw new IllegalArgumentException("Deposit amount must be positive");
//        }
//        this.balance = this.balance.add(amount);
//        // 记录交易
//        transactions.add(new Transaction("存款", amount.getValue(), this.balance.getValue()));
//    }

    public void deposit(Money amount) {
        if (amount.getValue() <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
//        notificationService.send("开始处理存款");
        this.balance = this.balance.add(amount);
        // 记录交易
        transactions.add(new Transaction("存款", amount.getValue(), this.balance.getValue()));
        // 调用通知服务
//        notificationService.notify("存款成功，当前余额：" + this.balance.getValue());
//        boolean sent = notificationService.send("存款成功，当前余额：" + this.balance.getValue());
//        if(!sent) System.out.println("通知发送失败，但存款已成功");
        try {
            notificationService.send("存款成功，当前余额：" + this.balance.getValue());
        } catch (Exception e) {
            // 记录日志，但不影响存款结果
            System.out.println("通知发送失败，但存款已成功：" + e.getMessage());
        }
    }

     public void withdraw(Money amount) {
         if (amount.getValue() <= 0) {
             throw new IllegalArgumentException("Withdraw amount cannot be negative");
         }
         if (this.balance.getValue() < amount.getValue()) {
             throw new IllegalArgumentException("Insufficient funds");
         }
         this.balance = this.balance.substract(amount);
         // 记录交易（金额为负数表示取款）
         transactions.add(new Transaction("取款", -amount.getValue(), this.balance.getValue()));
     }

     public void transfer(Money amount, Account target){
        this.withdraw(amount);
        target.deposit(amount);
     }

    public String printBalanceSlip() {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return "=== 余额单 ===\n" +
                "账户余额: " + balance.getValue() + " 元\n" +
                "打印时间: " + currentTime + "\n";
    }

    public String printStatement() {
        if (transactions.isEmpty()) {
            return "=== 交易对账单 ===\n暂无交易记录";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== 交易对账单 ===\n");
        sb.append("日期时间              类型    金额    余额\n");
        for (Transaction t : transactions) {
            sb.append(t.format()).append("\n");
        }
        return sb.toString();
    }
}