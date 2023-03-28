package ExpensesTracker.ExpensesTracker.services.income;
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

    public List<IncomeSource> getAllIncomeSourcesByUserId(Long userId) {
        return incomeSourceRepo.findAllByUserIdOrderByIncomeSourceName(userId);
    }

    @Transactional
    public void saveIncomeSource(IncomeSource incomeSource)
            throws IllegalArgumentException {
        incomeSourceRepo.save(incomeSource);
    }
}
