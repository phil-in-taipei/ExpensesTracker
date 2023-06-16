package ExpensesTracker.ExpensesTracker.controllers.expenses;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.models.expenses.Expense;
import ExpensesTracker.ExpensesTracker.models.expenses.SpendingRecord;
import ExpensesTracker.ExpensesTracker.models.expenses.forms.SpendingRecordForm;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.services.currency.CurrencyService;
import ExpensesTracker.ExpensesTracker.services.expenses.ExpenseService;
import ExpensesTracker.ExpensesTracker.services.expenses.SpendingRecordService;
import ExpensesTracker.ExpensesTracker.services.users.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import java.math.BigDecimal;
import java.time.*;
import java.util.List;
@Controller
public class SpendingRecordController {

    @Autowired
    CurrencyService currencyService;

    @Autowired
    ExpenseService expenseService;

    @Autowired
    SpendingRecordService spendingRecordService;

    @Autowired
    UserDetailsServiceImp userService;


    @GetMapping("/user-expenditures-current-month")
    public String showAllUserSpendingRecordsForCurrentMonth(
            Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
        LocalDate today = LocalDate.now();
        Month month = today.getMonth();
        Year year = Year.of(today.getYear());
        LocalDate monthBegin = today.withDayOfMonth(1);
        LocalDate monthEnd = today.plusMonths(1)
                .withDayOfMonth(1).minusDays(1);
        List<SpendingRecord> spendingRecords = spendingRecordService
                .getAllUserSpendingRecordsInDateRange(
                        user.getUsername(), monthBegin, monthEnd);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("spendingRecords", spendingRecords);
        model.addAttribute("user", user);
        return "expenses/user-spending-records-by-month";
    }

    @GetMapping("/record-expenditure")
    public String showRecordExpenditureFormPage(
            Authentication authentication, Model model) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        SpendingRecordForm spendingRecord = new SpendingRecordForm();
        model.addAttribute("spendingRecord", spendingRecord);
        List<Currency> currencies = currencyService.getAllCurrencies();
        model.addAttribute("currencies", currencies);
        List<Expense> expenses = expenseService
                .getAllExpensesByUserUsername(user.getUsername());
        model.addAttribute("expenses", expenses);
        return "expenses/record-expenditure";
    }

    @PostMapping("/submit-spending-record")
    public String submitNewSpendingRecord(
            @ModelAttribute("spendingRecord")
            SpendingRecordForm spendingRecordForm,
            Model model, Authentication authentication) {
        try {
            BigDecimal amount = spendingRecordForm.getAmount();
            Currency currency = currencyService.getCurrency(spendingRecordForm.getCurrencyId());
            LocalDate date = LocalDate.parse(spendingRecordForm.getDate());
            Expense expense = expenseService.getExpense(spendingRecordForm.getExpenseId());
            SpendingRecord spendingRecord = new SpendingRecord();
            spendingRecord.setAmount(amount);
            spendingRecord.setCurrency(currency);
            spendingRecord.setDate(date);
            spendingRecord.setExpense(expense);
            spendingRecordService.saveSpendingRecord(spendingRecord);
        } catch (IllegalArgumentException e) {
            model.addAttribute(
                    "message",
                    "Could not record new spending record, "
                            + e.getMessage());
            return "error/error";
        }
        return "redirect:/user-expenditures-current-month";
    }
}
