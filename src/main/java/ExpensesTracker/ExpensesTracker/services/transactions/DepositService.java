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

    @Loggable
    public void deleteDeposit(Long id) {
        depositRepo.deleteById(id);
    }

    // for user to search deposits by savings account, month, and year
    @Loggable
    public List<Deposit> getAllDepositsBySavingsAccountInDateRange(
            Long accountId, LocalDate firstDate, LocalDate lastDate) {
        return depositRepo
                .findAllBySavingsAccountIdAndDateBetweenOrderByDateAsc(
                        accountId, firstDate, lastDate
                );
    }

    // for user to search for all deposits in all of users' savings account
    // by month and year
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
