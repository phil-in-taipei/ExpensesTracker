package ExpensesTracker.ExpensesTracker.services.accounts;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.repositories.accounts.SavingAccountsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SavingsAccountService {

    @Autowired
    SavingAccountsRepo savingAccountsRepo;

    public void deleteSavingsAccount(Long id) {
        savingAccountsRepo.deleteById(id);
    }

    public List<SavingsAccount> getAllAccountsByUserId(Long userId) {
        return savingAccountsRepo
                .findAllByUserIdOrderByBank_BankNameAsc(userId);
    }

    public List<SavingsAccount> getAllAccountsByUserUsername(String username) {
        return savingAccountsRepo
                .findAllByUserUsernameOrderByBank_BankNameAsc(username);
    }

    public SavingsAccount getSavingsAccount(Long id) {
        return savingAccountsRepo.findById(id)
                .orElse(null);
    }

    @Transactional
    public void saveSavingsAccount(SavingsAccount savingsAccount)
            throws IllegalArgumentException {
        savingAccountsRepo.save(savingsAccount);
    }
}
