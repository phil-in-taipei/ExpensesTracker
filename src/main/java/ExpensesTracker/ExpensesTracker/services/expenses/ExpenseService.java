package ExpensesTracker.ExpensesTracker.services.expenses;
import ExpensesTracker.ExpensesTracker.models.expenses.Expense;
import ExpensesTracker.ExpensesTracker.repositories.expenses.ExpenseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    ExpenseRepo expenseRepo;

    public List<Expense> getAllExpensesByUserUsername(
            String username) {
        return expenseRepo
                .findAllByUserUsernameOrderByExpenseName(username);
    }

    @Transactional
    public Expense saveExpense(Expense expense)
            throws IllegalArgumentException {
        return expenseRepo.save(expense);
    }
}
