package splitter.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import splitter.data.Transaction;
import splitter.data.TransactionRepository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class BalanceTracker {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private GroupManager groupManager;

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

    @Transactional
    public void dropTransactionByDate(LocalDate date){
        transactionRepository.deleteTransactionsByDate(date);
    }

    public List<BalanceSummary> getBalanceSummary(LocalDate date, List<String> members) throws EmptyGroupException, GroupNotFoundException {
        Stream<BalanceSummary> summariesStream = transactionRepository.getBalanceSummaries(date).stream()
                .peek(summary -> {
                    if (summary.getAmount().signum() == -1) summary.invert();
                });

        if(members != null && !members.isEmpty()) {
            Set<String> finalMembers = groupManager.getFinalSpecifiedMembers(members);
            if (finalMembers.isEmpty()) throw new EmptyGroupException();
            summariesStream = summariesStream.filter(summary -> finalMembers.contains(summary.getPerson1()));
        }
        return summariesStream.collect(Collectors.toUnmodifiableList());
    }
}
