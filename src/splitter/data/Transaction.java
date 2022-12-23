package splitter.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Transaction {
    @Id
    @GeneratedValue
    private Long id;
    private LocalDate date;
    private String person1;
    private String person2;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    public Transaction() {
    }

    public Transaction(String person1, String person2, BigDecimal amount) {
        this.date = LocalDate.now();
        this.person1 = person1;
        this.person2 = person2;
        this.amount = amount;
    }

    public Transaction(LocalDate date, String person1, String person2, BigDecimal amount) {
        this.date = date;
        this.person1 = person1;
        this.person2 = person2;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getPerson1() {
        return person1;
    }

    public void setPerson1(String person1) {
        this.person1 = person1;
    }

    public String getPerson2() {
        return person2;
    }

    public void setPerson2(String person2) {
        this.person2 = person2;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
