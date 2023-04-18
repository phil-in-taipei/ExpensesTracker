package ExpensesTracker.ExpensesTracker.repositories.accounts;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingAccountsRepo extends JpaRepository<SavingsAccount, Long> {
    List<SavingsAccount> findAllByUserIdOrderByBank_BankNameAsc(Long id);

    List<SavingsAccount>  findAllByUserUsernameOrderByBank_BankNameAsc(String username);
}
