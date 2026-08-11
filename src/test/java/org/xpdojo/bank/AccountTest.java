package org.xpdojo.bank;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;      // 用于 assertThat
import static org.junit.jupiter.api.Assertions.assertThrows;   // 用于 assertThrows
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Disabled
public class AccountTest {

    @Test
    // @Disabled // 暂时跳过测试用这个
    //测试存款功能
    public void depositShouldIncreaseBalance() {
        // 1. 创建一个新账户
        Account account = new Account();
        // 2. 存入 10 元
        account.deposit(new Money(10));
        // 3. 验证余额是否为 10
        assertThat(account.getBalance()).isEqualTo(new Money(10));
    }
    //测试存款负数
    @Test
    public void depositNegativeAmountShouldThrowException() {
        Account account = new Account();
        assertThrows(IllegalArgumentException.class, () -> account.deposit(new Money(-5)));//预期抛出异常
    }
    //测试取款功能
    @Test
    public void withdrawShouldDecreaseBalance(){
        Account account = new Account();
        account.deposit(new Money(20));
        account.withdraw(new Money(10));
        assertThat(account.getBalance()).isEqualTo(new Money(10));
    }
    //转账测试正常
    @Test
    @DisplayName("转账成功：从源账户转出金额，目标账户收到金额") //为测试方法提供可读性更好的中文描述，测试报告会显示这个名称
    public void transferShouldMoveMoneyBetweenAccounts(){
        Account source = new Account();
        source.deposit(new Money(50));
        Account target = new Account();
        target.deposit(new Money(10));

        source.transfer(new Money(20), target);

        assertThat(source.getBalance()).isEqualTo(new Money(30));
        assertThat(target.getBalance()).isEqualTo(new Money(30));
    }
    //转账边界条件测试--参数化测试
    @ParameterizedTest // 参数化测试方法，告诉JUnit这个方法需要运行多次，每次使用不同的参数
    @DisplayName("转账边界场景测试")
    @CsvSource({
            "0, 转账金额为0时，余额应保持不变",
            "100, 转账金额等于余额时，余额变为0",
            "30, 转账金额小于余额时，正常转账"
    }) // 上面参数来源的一种，以CSV格式(逗号分隔值，也可以delimiter属性指定)提供测试数据，依次映射到测试方法的参数上
    // JUnit会尝试将字符串转换为方法参数的类型，如int、long、boolean等，转换失败测试会报错；参数需要为null可以留空；转义字符用单引号包裹
    //JUnit还支持的参数来源：@MethodSource(从方法返回数据)、@EnumSource(枚举值)等
    public void transferBoundaryScenarios(int amount, String description){
        Account source = new Account();
        source.deposit(new Money(100));
        Account target = new Account();

        source.transfer(new Money(amount), target);

        assertThat(source.getBalance()).isEqualTo(new Money(100 - amount));
        assertThat(target.getBalance()).isEqualTo(new Money(amount));
    }
    //测试转账失败场景（余额不足）
    @ParameterizedTest
    @DisplayName("转账失败：余额不足应抛出异常")
    @CsvSource({
            "101, 超过余额1元",
            "200, 超过余额100元"
    })
    public void transferShouldFailWhenInsufficientFunds(int amount, String description){
        Account source = new Account();
        source.deposit(new Money(100));
        Account target = new Account();

        assertThrows(IllegalArgumentException.class,
                () -> source.transfer(new Money(amount), target));
    }
    //Money类equals()的分支测试补充
    @Test
    @DisplayName("Money与null比较应返回false")
    public void moneyEqualsNullShouldReturnFalse(){
        Money money = new Money(10);
//        assertThat(money.equals(null)).isFalse();
        assertThat(money).isNotEqualTo(null);
    }
    @Test
    @DisplayName("Money与不同类型比较应返回false")
    public void moneyEqualsDifferentTypeShouldReturnFalse(){
        Money money = new Money(10);
        assertThat(money.equals("not a money")).isFalse();
    }
    @ParameterizedTest
    @DisplayName("金额不同的Money对象应不相等，金额相同的Money对象应相等")
    @CsvSource({
            "10, 10, true",
            "10, 20, false"
    })
    public void moneyEqualsDifferentAmountShouldReturnFalse(int amount1, int amount2, boolean excepted){
        Money money1 = new Money(amount1);
        Money money2 = new Money(amount2);
        assertThat(money1.equals(money2)).isEqualTo(excepted);
    }

}
