package splitter.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BalanceTracker {

    private List<Transaction> transactions = new ArrayList<>();

    public void storeTransaction(LocalDate date, String person1, String person2, int amount) {
        if (person1.compareTo(person2) < 0) {
            transactions.add(new Transaction(date, person1, person2, amount));
        } else {
            transactions.add(new Transaction(date, person2, person1, -amount));
        }
    }

    public List<BalanceSummary> getBalanceSummary(LocalDate date) {
        Map<BalanceSummary, BalanceSummary> summaryMap = new HashMap<>();
        transactions.stream()
                .filter(transaction -> !transaction.getDate().isAfter(date))
                .forEach(transaction -> {
                    BalanceSummary summary = new BalanceSummary(transaction);
                    if (!summaryMap.containsKey(summary)) {
                        summaryMap.put(summary, summary);
                    } else {
                        summaryMap.get(summary).merge(summary);
                    }
                });


        return summaryMap.values().stream()
                .filter(summary -> summary.getAmount() != 0)
                .peek(summary -> {
                    if (summary.getAmount() < 0) summary.invert();
                }).collect(Collectors.toUnmodifiableList());
    }
}
