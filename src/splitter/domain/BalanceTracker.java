package splitter.domain;

import org.springframework.stereotype.Service;
import splitter.data.Transaction;
import splitter.data.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BalanceTracker {
    private final TransactionRepository transactionRepository;

    public BalanceTracker(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void storeTransaction(LocalDate date, String person1, String person2, BigDecimal amount) {
        if (person1.compareTo(person2) < 0) {
            transactionRepository.save(new Transaction(date, person1, person2, amount));
        } else {
            transactionRepository.save(new Transaction(date, person2, person1, amount.negate()));
        }
    }

    public void storeTransaction(Transaction transaction) {
        storeTransaction(transaction.getDate(),
                transaction.getPerson1(),
                transaction.getPerson2(),
                transaction.getAmount());
    }

    public List<BalanceSummary> getBalanceSummary(LocalDate date) {
        return transactionRepository.getBalanceSummaries(date).stream()
                .peek(summary -> {
                    if (summary.getAmount().signum() == -1) summary.invert();
                }).collect(Collectors.toUnmodifiableList());
    }
}
