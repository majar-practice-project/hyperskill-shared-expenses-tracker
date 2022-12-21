package splitter.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private LocalDate date;
    private String giverName;
    private String receiverName;
    private BigDecimal amount;

    public Transaction(String giverName, String receiverName, BigDecimal amount) {
        this.date = LocalDate.now();
        this.giverName = giverName;
        this.receiverName = receiverName;
        this.amount = amount;
    }

    public Transaction(LocalDate date, String giverName, String receiverName, BigDecimal amount) {
        this.date = date;
        this.giverName = giverName;
        this.receiverName = receiverName;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getGiverName() {
        return giverName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
