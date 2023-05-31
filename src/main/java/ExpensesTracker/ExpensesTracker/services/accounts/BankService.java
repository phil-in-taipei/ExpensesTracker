package ExpensesTracker.ExpensesTracker.services.accounts;

import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.repositories.accounts.BankRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BankService {

    @Autowired
    BankRepo bankRepo;

    public void deleteBank(Long id) {
        bankRepo.deleteById(id);
    }

    public List<Bank> getAllBanks() {
        return bankRepo.findAllByOrderByBankName();
    }

    public Bank getBank(Long id) {
        return bankRepo.findById(id)
                .orElse(null);
    }

    @Transactional
    public Bank saveBank(Bank bank)
            throws IllegalArgumentException {
        return bankRepo.save(bank);
    }
}
