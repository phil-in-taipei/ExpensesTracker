package ExpensesTracker.ExpensesTracker.repositories.transactions;

import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.transactions.Deposit;
import ExpensesTracker.ExpensesTracker.models.transactions.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WithdrawalRepo extends JpaRepository<Deposit, Long> {
    List<Withdrawal> findAllBySavingsAccount_UserUsernameAndDateBetweenOrderByDateDesc(
            String username, LocalDate firstDate, LocalDate lastDate);

    List<Withdrawal> findAllBySavingsAccountIdAndDateBetweenOrderByDateDesc(
            String username, LocalDate firstDate, LocalDate lastDate);
}
