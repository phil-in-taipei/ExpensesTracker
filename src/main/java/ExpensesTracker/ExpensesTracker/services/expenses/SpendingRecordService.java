package ExpensesTracker.ExpensesTracker.services.expenses;
import ExpensesTracker.ExpensesTracker.logging.Loggable;
import ExpensesTracker.ExpensesTracker.models.expenses.Expense;
import ExpensesTracker.ExpensesTracker.models.expenses.SpendingRecord;
import ExpensesTracker.ExpensesTracker.repositories.expenses.SpendingRecordRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SpendingRecordService {

    @Autowired
    SpendingRecordRepo spendingRecordRepo;

    @Loggable
    public List<SpendingRecord> getAllUserSpendingRecordsInDateRange(
            String username, LocalDate firstDate, LocalDate lastDate) {
        return spendingRecordRepo
                .findAllByExpense_UserUsernameAndDateBetweenOrderByDateAsc(
                        username, firstDate, lastDate
                );
    }

    public SpendingRecord getSpendingRecord(Long id) {
        return spendingRecordRepo.findById(id)
                .orElse(null);
    }

    @Transactional
    public SpendingRecord saveSpendingRecord(SpendingRecord spendingRecord)
            throws IllegalArgumentException {
        return spendingRecordRepo.save(spendingRecord);
    }
}
