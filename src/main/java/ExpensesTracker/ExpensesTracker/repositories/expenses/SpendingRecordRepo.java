package ExpensesTracker.ExpensesTracker.repositories.expenses;

import ExpensesTracker.ExpensesTracker.models.expenses.Expense;
import ExpensesTracker.ExpensesTracker.models.expenses.SpendingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SpendingRecordRepo extends JpaRepository<SpendingRecord, Long> {
    List<SpendingRecord> findAllByExpense_UserUsernameAndDateBetweenOrderByDateAsc(
            String username, LocalDate firstDate, LocalDate lastDate);
}
