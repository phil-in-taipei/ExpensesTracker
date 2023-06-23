package ExpensesTracker.ExpensesTracker.repositories.transactions;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.transactions.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DepositRepo extends JpaRepository<Deposit, Long> {
    List<SavingsAccount> findAllBySavingsAccount_UserUsernameAndDateBetweenOrderByDateDesc(
            String username, LocalDate firstDate, LocalDate lastDate);

    List<SavingsAccount> findAllBySavingsAccountAndDateBetweenOrderByDateDesc(
            String username, LocalDate firstDate, LocalDate lastDate);
}
