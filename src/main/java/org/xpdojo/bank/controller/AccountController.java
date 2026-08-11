package org.xpdojo.bank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xpdojo.bank.Account;
import org.xpdojo.bank.DefaultNotificationService;
import org.xpdojo.bank.Money;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private Account account = new Account();
//    private Account account = new Account(new DefaultNotificationService());

    @PostMapping("/deposit")
    public String deposit(@RequestParam("amount") int amount){
//        account.deposit(new Money(amount));
//        return "存款成功，当前余额：" + account.getBalance().getValue();
        try {
            account.deposit(new Money(amount));
            return "存款成功，当前余额：" + account.getBalance().getValue();
        } catch (IllegalArgumentException e) {
            return "存款失败：" + e.getMessage();
        }
    }

//    @PostMapping("/withdraw")
//    public String withdraw(@RequestParam("amount") int amount){
////        account.withdraw(new Money(amount));
////        return "取款成功，当前余额：" + account.getBalance().getValue();
//        try {
//            account.withdraw(new Money(amount));
//            return "取款成功，当前余额：" + account.getBalance().getValue();
//        } catch (IllegalArgumentException e){
//            // 捕获并返回友好的错误信息
//            return "取款失败：" + e.getMessage();
//        }
//    }
    // 希望HTTP状态码反应请求结果（RESTful风格） -> 在异常时返回400
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestParam("amount") int amount) {
        try {
            account.withdraw(new Money(amount));
            return ResponseEntity.ok("取款成功，当前余额：" + account.getBalance().getValue());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("取款失败：" + e.getMessage());
        }
    }

    @GetMapping("/balance")
    public String balance(){
        return "当前余额：" + account.getBalance().getValue();
    }

    @GetMapping("/statement")
    public String statement(){
        return account.printStatement();
    }

    // 重置接口
    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        account = new Account();  // 重新创建一个空账户
        return ResponseEntity.ok("账户已重置");
    }
}
