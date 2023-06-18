package ExpensesTracker.ExpensesTracker.controllers.expenses;
import ExpensesTracker.ExpensesTracker.models.expenses.Expense;
import ExpensesTracker.ExpensesTracker.models.income.IncomeSource;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.services.expenses.ExpenseService;
import ExpensesTracker.ExpensesTracker.services.users.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ExpensesController {

    @Autowired
    ExpenseService expenseService;

    @Autowired
    UserDetailsServiceImp userService;

    @GetMapping("/create-expense")
    public String showCreateExpensePage(
            Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
        Expense expense = new Expense();
        model.addAttribute("user", user);
        model.addAttribute("expense", expense);
        return "expenses/create-expense";
    }

    @RequestMapping("/delete-expense/{expenseId}")
    public String deleteExpense(
            @PathVariable(name = "expenseId")
            Long expenseId, Model model) {
        if (expenseService.getExpense(expenseId) == null) {
            model.addAttribute("message",
                    "Cannot delete, expense with id: "
                            + expenseId + " does not exist.");
            return "error/error";
        }
        expenseService.deleteExpense(expenseId);
        return "redirect:/user-expenses";
    }

    @PostMapping("/submit-expense")
    public String saveNewExpense(
            @ModelAttribute("expense")
            Expense expense, Model model, Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
            expense.setUser(user);
            expenseService.saveExpense(expense);
        } catch (IllegalArgumentException e) {
            model.addAttribute(
                    "message",
                    "Could not save expense, "
                            + e.getMessage());
            return "error/error";
        }
        return "redirect:/user-expenses";
    }

    @GetMapping("/user-expenses")
    public String showAllUsersExpenses(
            Authentication authentication, Model model){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
        List<Expense> expenses = expenseService
                .getAllExpensesByUserUsername(user.getUsername());
        model.addAttribute("expenses", expenses);
        return "expenses/user-expenses";
    }
}
