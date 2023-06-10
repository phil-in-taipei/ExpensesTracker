package ExpensesTracker.ExpensesTracker.services.income;
import ExpensesTracker.ExpensesTracker.models.expenses.Expense;
import ExpensesTracker.ExpensesTracker.models.income.IncomeSource;
import ExpensesTracker.ExpensesTracker.repositories.income.IncomeSourceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IncomeSourceService {

    @Autowired
    IncomeSourceRepo incomeSourceRepo;

    public void deleteIncomeSource(Long id) {
        incomeSourceRepo.deleteById(id);
    }

    public List<IncomeSource> getAllIncomeSourcesByUserUsername(String username) {
        return incomeSourceRepo.findAllByUserUsernameOrderByIncomeSourceName(username);
    }

    public IncomeSource getIncomeSource(Long id) {
        return incomeSourceRepo.findById(id)
                .orElse(null);
    }

    @Transactional
    public IncomeSource saveIncomeSource(IncomeSource incomeSource)
            throws IllegalArgumentException {
        return incomeSourceRepo.save(incomeSource);
    }
}
