package ExpensesTracker.ExpensesTracker.services.accounts;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.repositories.accounts.SavingAccountsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SavingsAccountService {

    @Autowired
    SavingAccountsRepo savingAccountsRepo;

    public SavingsAccount depositMoneyIntoAccount(
            SavingsAccount savingsAccount, BigDecimal withdrawalAmount) {
        System.out.println("This is the deposit amount: " + withdrawalAmount);
        BigDecimal savingsAccountBalance = savingsAccount.getAccountBalance();
        System.out.println("This is the previous account balance: " + savingsAccountBalance);
        BigDecimal newAccountBalance = savingsAccountBalance.add(withdrawalAmount);
        System.out.println("This is the new account balance: " + newAccountBalance);
        savingsAccount.setAccountBalance(
                newAccountBalance
        );
        return savingsAccount;
    }

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
    public SavingsAccount saveSavingsAccount(SavingsAccount savingsAccount)
            throws IllegalArgumentException {
        return savingAccountsRepo.save(savingsAccount);
    }

    public SavingsAccount withdrawMoneyFromAccount(
            SavingsAccount savingsAccount, BigDecimal withdrawalAmount) {
        System.out.println("This is the withdrawal amount: " + withdrawalAmount);
        BigDecimal savingsAccountBalance = savingsAccount.getAccountBalance();
        System.out.println("This is the previous account balance: " + savingsAccountBalance);
        BigDecimal newAccountBalance = savingsAccountBalance.subtract(withdrawalAmount);
        System.out.println("This is the new account balance: " + newAccountBalance);
        savingsAccount.setAccountBalance(
                newAccountBalance
        );
        return savingsAccount;
    }
}
