package splitter.domain;

import splitter.data.Transaction;

import java.math.BigDecimal;
import java.util.Objects;

public class BalanceSummary {
    private String person1;
    private String person2;
    private BigDecimal amount;

    public BalanceSummary(String person1, String person2, BigDecimal amount) {
        this.person1 = person1;
        this.person2 = person2;
        this.amount = amount;
    }

    public void merge(BalanceSummary that) {
        amount = amount.add(that.amount);
    }

    public void invert() {
        String temp = person1;
        person1 = person2;
        person2 = temp;
        amount = amount.negate();
    }

    public String getPerson1() {
        return person1;
    }

    public String getPerson2() {
        return person2;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BalanceSummary that = (BalanceSummary) o;
        return person1.equals(that.person1) && person2.equals(that.person2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(person1, person2);
    }
}
