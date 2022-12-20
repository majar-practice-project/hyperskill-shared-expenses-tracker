package splitter.domain;

import java.util.Objects;

public class BalanceSummary {
    private String person1;
    private String person2;
    private int amount;

    public BalanceSummary(Transaction transaction){
        person1 = transaction.getGiverName();
        person2 = transaction.getReceiverName();
        amount = transaction.getAmount();
    }

    public void merge(BalanceSummary that){
        amount += that.amount;
    }

    public void invert(){
        String temp = person1;
        person1 = person2;
        person2 = temp;
        amount = -amount;
    }

    public String getPerson1() {
        return person1;
    }

    public String getPerson2() {
        return person2;
    }

    public int getAmount() {
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
