package ExpensesTracker.ExpensesTracker.services.transactions;
import ExpensesTracker.ExpensesTracker.logging.Loggable;
import ExpensesTracker.ExpensesTracker.models.transactions.Deposit;
import ExpensesTracker.ExpensesTracker.models.transactions.Withdrawal;
import ExpensesTracker.ExpensesTracker.repositories.transactions.WithdrawalRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class WithdrawalService {

    @Autowired
    WithdrawalRepo withdrawalRepo;

    public void deleteWithdrawal(Long id) {
        withdrawalRepo.deleteById(id);
    }

    @Loggable
    public List<Withdrawal> getAllUserWithdrawalsInDateRange(
            String username, LocalDate firstDate, LocalDate lastDate) {
        return withdrawalRepo
                .findAllBySavingsAccount_UserUsernameAndDateBetweenOrderByDateAsc(
                        username, firstDate, lastDate
                );
    }


    @Loggable
    public List<Withdrawal> getAllWithdrawalsBySavingsAccountInDateRange(
            Long accountId, LocalDate firstDate, LocalDate lastDate) {
        return withdrawalRepo
                .findAllBySavingsAccountIdAndDateBetweenOrderByDateAsc(
                        accountId, firstDate, lastDate
                );
    }

    @Loggable
    public Withdrawal getWithdrawal(Long id) {
        return withdrawalRepo.findById(id)
                .orElse(null);
    }

    @Loggable
    @Transactional
    public Withdrawal saveWithdrawal(Withdrawal withdrawal)
            throws IllegalArgumentException {
        return withdrawalRepo.save(withdrawal);
    }
}
