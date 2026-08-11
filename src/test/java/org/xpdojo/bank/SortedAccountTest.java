package org.xpdojo.bank;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SortedAccountTest {

    // ==================== 存款功能测试 ====================
    @ParameterizedTest
    @DisplayName("[BA_DEP] 存款：各种正数金额场景的余额变化验证")
    @CsvSource({
            "10, 10, 正常存款后余额正确",
            "1, 1, 存款最小正数1元后余额正确"
    })
    void shouldUpdateBalanceCorrectlyWhenDeposit(int depositAmount, int expectedBalance, String description) {
        Account account = new Account();
        account.deposit(new Money(depositAmount));
        assertThat(account.getBalance()).isEqualTo(new Money(expectedBalance));
    }

    @Test
    @DisplayName("[BA_DEP_001] 存款异常：存入金额为0，应抛出异常")
    void shouldNotChangeBalanceWhenDepositZero() {
        Account account = new Account();
        assertThrows(IllegalArgumentException.class,
                () -> account.deposit(new Money(0)));
    }

    @Test
    @DisplayName("[BA_DEP_002] 存款异常：存入负数，应抛出异常")
    void shouldThrowExceptionWhenDepositNegativeAmount() {
        Account account = new Account();
        assertThrows(IllegalArgumentException.class,
                () -> account.deposit(new Money(-5)));
    }

    @Test
    @DisplayName("[BA_DEP_004] 存款边界：存入最小正数金额，余额应增加")
    void shouldIncreaseBalanceWhenDepositMinimumPositiveAmount() {
        Account account = new Account();
        account.deposit(new Money(1));
        assertThat(account.getBalance()).isEqualTo(new Money(1));
    }

    // ==================== 取款功能测试 ====================

    @ParameterizedTest
    @DisplayName("[BA_WTH] 取款：各种合法金额场景的余额变化验证")
    @CsvSource({
            "20, 10, 10, 取款10元后余额减少10",
            "100, 100, 0, 取款等于余额，余额变为0",
            "100, 1, 99, 取款最小正数1元，余额减少1"
    })
    void shouldUpdateBalanceCorrectlyWhenWithdraw(int initialDeposit, int withdrawAmount, int expectedBalance, String description) {
        Account account = new Account();
        account.deposit(new Money(initialDeposit));
        account.withdraw(new Money(withdrawAmount));
        assertThat(account.getBalance()).isEqualTo(new Money(expectedBalance));
    }

    @Test
    @DisplayName("[BA_WTH_EXC_001] 取款异常：取款金额超过余额")
    void shouldThrowExceptionWhenWithdrawExceedsBalance() {
        Account account = new Account();
        account.deposit(new Money(100));
        assertThrows(IllegalArgumentException.class,
                () -> account.withdraw(new Money(101)));
    }

    @Test
    @DisplayName("[BA_WTH_EXC_002] 取款异常：取款金额为0")
    void shouldThrowExceptionWhenWithdrawZero() {
        Account account = new Account();
        account.deposit(new Money(100));
        assertThrows(IllegalArgumentException.class,
                () -> account.withdraw(new Money(0)));
    }

    @Test
    @DisplayName("[BA_WTH_EXC_003] 取款异常：取款金额为负数")
    void shouldThrowExceptionWhenWithdrawNegative() {
        Account account = new Account();
        account.deposit(new Money(100));
        assertThrows(IllegalArgumentException.class,
                () -> account.withdraw(new Money(-10)));
    }

    // ==================== 转账功能测试 ====================
    @ParameterizedTest
    @DisplayName("转账边界场景测试（正数金额）")
    @CsvSource({
            "100, 转账金额等于余额时，余额变为0",
            "30, 转账金额小于余额时，正常转账"
    })
    void transferBoundaryScenarios(int amount, String description) {
        Account source = new Account();
        source.deposit(new Money(100));
        Account target = new Account();
        source.transfer(new Money(amount), target);
        assertThat(source.getBalance()).isEqualTo(new Money(100 - amount));
        assertThat(target.getBalance()).isEqualTo(new Money(amount));
    }

    @Test
    @DisplayName("[BA_TRF_EXC_001] 转账异常：转账金额为0")
    void shouldThrowExceptionWhenTransferZero() {
        Account source = new Account();
        source.deposit(new Money(100));
        Account target = new Account();
        assertThrows(IllegalArgumentException.class,
                () -> source.transfer(new Money(0), target));
    }

    @Test
    @DisplayName("[BA_TRF_EXC_002] 转账异常：转账金额超过余额")
    void shouldThrowExceptionWhenTransferExceedsBalance() {
        Account source = new Account();
        source.deposit(new Money(100));
        Account target = new Account();
        assertThrows(IllegalArgumentException.class,
                () -> source.transfer(new Money(101), target));
    }

    // ==================== Money类equals()的分支测试补充 ====================
    @Test
    @DisplayName("Money与null比较应返回false")
    public void moneyEqualsNullShouldReturnFalse(){
        Money money = new Money(10);
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
    @Test
    @DisplayName("Money 对象与自身比较应返回 true（引用相等）")
    void shouldReturnTrueWhenComparingSameInstance() {
        Money money = new Money(10);
        assertThat(money.equals(money)).isTrue();
    }

    // ==================== 余额单打印功能测试 ====================

    @Test
    @DisplayName("[BA_BAL_001] 余额为0时，打印余额单显示余额为0")
    void shouldPrintBalanceSlipWithZeroBalance() {
        Account account = new Account();
        String slip = account.printBalanceSlip();
        assertThat(slip).contains("0 元");
    }

    @Test
    @DisplayName("[BA_BAL_002] 余额为正数时，打印余额单显示正确余额")
    void shouldPrintBalanceSlipWithPositiveBalance() {
        Account account = new Account();
        account.deposit(new Money(100));
        String slip = account.printBalanceSlip();
        assertThat(slip).contains("100 元");
    }

    @Test
    @DisplayName("[BA_BAL_003] 打印余额单包含日期时间信息")
    void shouldPrintBalanceSlipContainDateTime() {
        Account account = new Account();
        account.deposit(new Money(50));
        String slip = account.printBalanceSlip();
        // 简单验证是否包含日期模式（如2026-08-04 或 2026/08/04）
//        assertThat(slip).matches(".*\\d{4}-\\d{2}-\\d{2}.*"); // 匹配 YYYY-MM-DD 格式
        assertThat(slip).containsPattern("\\d{4}-\\d{2}-\\d{2}"); // 匹配 YYYY-MM-DD 格式
        assertThat(slip).containsPattern("\\d{2}:\\d{2}:\\d{2}"); // 匹配 HH:mm:ss 格式
    }

    @Test
    @DisplayName("[BA_BAL_004] 多次打印余额单，时间更新但余额不变")
    void shouldUpdateTimestampOnEachPrint() {
        Account account = new Account();
        account.deposit(new Money(100));
        String slip1 = account.printBalanceSlip();
        // 等待至少 1 秒
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String slip2 = account.printBalanceSlip();
        assertThat(slip1).isNotEqualTo(slip2);
        assertThat(slip1).contains("100 元");
        assertThat(slip2).contains("100 元");
    }

    // ==================== 交易对账单功能测试 ====================

    @Test
    @DisplayName("[BA_STM_001] 无交易时，对账单显示为空或提示信息")
    void shouldShowEmptyStatementWhenNoTransactions() {
        Account account = new Account();
        String statement = account.printStatement();
        assertThat(statement).contains("暂无交易记录");
    }

    @Test
    @DisplayName("[BA_STM_002] 单笔存款后，对账单显示一条存款记录")
    void shouldShowSingleDepositInStatement() {
        Account account = new Account();
        account.deposit(new Money(100));
        String statement = account.printStatement();
        // 检查是否包含 "+100" 和行末的 "100"（余额）
        assertThat(statement).contains("存款");
        assertThat(statement).containsPattern("\\+100\\s+100");  // 金额 +100，后面跟空格，余额 100
    }

    @Test
    @DisplayName("[BA_STM_003] 多笔交易后，对账单按时间顺序显示所有交易")
    void shouldShowAllTransactionsInChronologicalOrder() {
        Account account = new Account();
        account.deposit(new Money(100));
        account.withdraw(new Money(30));
        account.deposit(new Money(50));
        String statement = account.printStatement();

        // 验证包含所有交易
        assertThat(statement).containsPattern("\\+100\\s+100");
        assertThat(statement).containsPattern("-30\\s+70");
        assertThat(statement).containsPattern("\\+50\\s+120");

        // 验证最终余额是 120（可以检查最后一行是否以 120 结尾）
        assertThat(statement).endsWith("120\n");}

    @Test
    @DisplayName("[BA_STM_004] 对账单中的日期时间格式正确")
    void shouldContainCorrectDateTimeFormatInStatement() {
        Account account = new Account();
        account.deposit(new Money(100));
        String statement = account.printStatement();

        // 使用正则表达式验证日期时间格式
        assertThat(statement).containsPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
    }
}
