package splitter.domain;

import java.time.LocalDate;

public class Transaction {
    private LocalDate date;
    private String giverName;
    private String receiverName;
    private int amount;

    public Transaction(String giverName, String receiverName, int amount) {
        this.date = LocalDate.now();
        this.giverName = giverName;
        this.receiverName = receiverName;
        this.amount = amount;
    }

    public Transaction(LocalDate date, String giverName, String receiverName, int amount) {
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

    public int getAmount() {
        return amount;
    }
}
