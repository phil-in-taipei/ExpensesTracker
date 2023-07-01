package ExpensesTracker.ExpensesTracker.services.transactions;

import ExpensesTracker.ExpensesTracker.logging.Loggable;
import ExpensesTracker.ExpensesTracker.models.expenses.Expense;
import ExpensesTracker.ExpensesTracker.models.transactions.Deposit;
import ExpensesTracker.ExpensesTracker.repositories.transactions.DepositRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DepositService {

    @Autowired
    DepositRepo depositRepo;

    public void deleteDeposit(Long id) {
        depositRepo.deleteById(id);
    }

    @Loggable
    public List<Deposit> getAllDepositsBySavingsAccountInDateRange(
            Long accountId, LocalDate firstDate, LocalDate lastDate) {
        return depositRepo
                .findAllBySavingsAccountIdAndDateBetweenOrderByDateAsc(
                        accountId, firstDate, lastDate
                );
    }

    @Loggable
    public List<Deposit> getAllUserDepositsInDateRange(
            String username, LocalDate firstDate, LocalDate lastDate) {
        return depositRepo
                .findAllBySavingsAccount_UserUsernameAndDateBetweenOrderByDateAsc(
                        username, firstDate, lastDate
                );
    }

    @Loggable
    public Deposit getDeposit(Long id) {
        return depositRepo.findById(id)
                .orElse(null);
    }

    @Loggable
    @Transactional
    public Deposit saveDeposit(Deposit deposit)
            throws IllegalArgumentException {
        return depositRepo.save(deposit);
    }
}
