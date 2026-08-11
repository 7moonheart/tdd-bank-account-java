package org.xpdojo.bank;

/**
 * Immutable class to represent Money as a concept.
 * This class should have no accessor methods.
 */
public class Money {
    private final int amount;

    public Money(int amount){
        this.amount = amount;
    }
    public Money add(Money other){
        return new Money(this.amount + other.amount);
    }
    public Money substract(Money other){
        return new Money(this.amount - other.amount);
    }
    public int getValue(){
        return amount;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;// 分支1：引用相等
        if (obj == null || getClass() != obj.getClass()) return false;// 分支2：null或类型不匹配
        Money money = (Money) obj;// 分支3：类型匹配
        return amount == money.amount;// 分支4：金额相等
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(amount);
    }
}
