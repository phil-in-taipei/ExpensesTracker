package ExpensesTracker.ExpensesTracker.services.accounts;

import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.repositories.accounts.BankRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BankService {

    @Autowired
    BankRepo bankRepo;

    public List<Bank> getAllBanks() {
        return bankRepo.findAllByOrderByBankName();
    }

    @Transactional
    public void saveBank(Bank bank)
            throws IllegalArgumentException {
        bankRepo.save(bank);
    }
}
