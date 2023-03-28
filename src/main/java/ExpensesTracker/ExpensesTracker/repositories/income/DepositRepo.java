package ExpensesTracker.ExpensesTracker.repositories.income;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.income.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DepositRepo extends JpaRepository<Deposit, Long> {
    List<SavingsAccount> findAllByUserIdOrderByTimeDsc(Long id);
}
