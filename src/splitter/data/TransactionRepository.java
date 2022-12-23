package splitter.data;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import splitter.domain.BalanceSummary;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    @Query("SELECT new splitter.domain.BalanceSummary(t.person1, t.person2, SUM(t.amount)) FROM Transaction t " +
            "WHERE t.date <= ?1 GROUP BY t.person1, t.person2 HAVING SUM(t.amount)<>0")
    List<BalanceSummary> getBalanceSummaries(LocalDate date);
}
