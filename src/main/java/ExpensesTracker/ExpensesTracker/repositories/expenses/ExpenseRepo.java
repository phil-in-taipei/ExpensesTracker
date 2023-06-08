package ExpensesTracker.ExpensesTracker.repositories.expenses;
import ExpensesTracker.ExpensesTracker.models.expenses.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepo extends JpaRepository<Expense, Long> {
    List<Expense> findAllByUserUsernameOrderByExpenseName(String username);
}
