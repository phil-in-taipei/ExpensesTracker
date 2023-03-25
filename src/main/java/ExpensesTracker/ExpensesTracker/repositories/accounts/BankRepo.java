package ExpensesTracker.ExpensesTracker.repositories.accounts;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BankRepo extends JpaRepository<Bank, Long> {
    List<Bank> findAllByOrderByBankName();
}
