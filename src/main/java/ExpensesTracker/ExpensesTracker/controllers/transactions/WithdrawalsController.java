package ExpensesTracker.ExpensesTracker.controllers.transactions;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.income.IncomeSource;
import ExpensesTracker.ExpensesTracker.models.search.SearchMonthAndYearForm;
import ExpensesTracker.ExpensesTracker.models.transactions.Deposit;
import ExpensesTracker.ExpensesTracker.models.transactions.Withdrawal;
import ExpensesTracker.ExpensesTracker.models.transactions.forms.DepositForm;
import ExpensesTracker.ExpensesTracker.models.transactions.forms.WithdrawalForm;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.services.accounts.SavingsAccountService;
import ExpensesTracker.ExpensesTracker.services.transactions.WithdrawalService;
import ExpensesTracker.ExpensesTracker.services.users.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;

@Controller
public class WithdrawalsController {

    @Autowired
    SavingsAccountService savingsAccountService;

    @Autowired
    UserDetailsServiceImp userService;

    @Autowired
    WithdrawalService withdrawalService;

    @PostMapping("/search-withdrawals-by-month-year")
    public String searchWithdrawalsByMonthAndYear(
            @ModelAttribute("searchMonthAndYear")
            SearchMonthAndYearForm searchMonthAndYear,
            Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
        Month month = searchMonthAndYear.getMonth();
        int queryMonth = month.getValue();
        int queryYear = searchMonthAndYear.getYear();
        LocalDate date = LocalDate.now();
        // finds local date for the first day of the specified month/year
        LocalDate monthBegin = date.withDayOfMonth(1).withMonth(queryMonth)
                .withYear(queryYear);
        // finds local date for the last day of the specified month/year
        LocalDate monthEnd = monthBegin.plusMonths(1)
                .withDayOfMonth(1).minusDays(1);
        List<Withdrawal> withdrawals = withdrawalService
                .getAllUserWithdrawalsInDateRange(user.getUsername(), monthBegin, monthEnd);
        model.addAttribute("year", queryYear);
        model.addAttribute("month", month);
        model.addAttribute("withdrawals", withdrawals);
        model.addAttribute("user", user);
        return "transactions/user-withdrawals-by-month.html";
    }


    @GetMapping("/user-withdrawals-current-month")
    public String showAllUserWithdrawalsForCurrentMonth(
            Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
        LocalDate today = LocalDate.now();
        Month month = today.getMonth();
        Year year = Year.of(today.getYear());
        LocalDate monthBegin = today.withDayOfMonth(1);
        LocalDate monthEnd = today.plusMonths(1)
                .withDayOfMonth(1).minusDays(1);
        List<Withdrawal> withdrawals = withdrawalService
                .getAllUserWithdrawalsInDateRange(user.getUsername(), monthBegin, monthEnd);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("withdrawals", withdrawals);
        model.addAttribute("user", user);
        return "transactions/user-withdrawals-by-month.html";
    }

    @GetMapping("/make-withdrawal")
    public String showMakeWithdrawalFormPage(
            Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        WithdrawalForm withdrawalForm = new WithdrawalForm();
        model.addAttribute("withdrawal", withdrawalForm);
        List<SavingsAccount> savingsAccounts = savingsAccountService
                .getAllAccountsByUserUsername(userDetails.getUsername());
        model.addAttribute("savingsAccounts", savingsAccounts);
        return "transactions/make-withdrawal.html";
    }

    @GetMapping("/search-withdrawals-by-month-and-year")
    public String showSearchWithdrawalsByMonthAndYearPage(Model model) {
        Month[] monthOptions = Month.values();
        model.addAttribute("monthOptions", monthOptions);
        model.addAttribute("searchMonthAndYear",
                new SearchMonthAndYearForm());
        return "transactions/search-withdrawals-by-month-and-year.html";
    }

    @PostMapping("/submit-withdrawal-form")
    public String submitNewWithdrawal(
            @ModelAttribute("withdrawal")
            WithdrawalForm withdrawalForm,
            Model model, Authentication authentication) {
        try {
            BigDecimal withdrawalAmount = withdrawalForm.getAmount();
            SavingsAccount savingsAccount = savingsAccountService
                    .getSavingsAccount(withdrawalForm.getSavingsAccountId());
            LocalDate date = LocalDate.parse(withdrawalForm.getDate());
            Withdrawal withdrawal = new Withdrawal();
            withdrawal.setAmount(withdrawalAmount);
            withdrawal.setSavingsAccount(savingsAccount);
            withdrawal.setDate(date);
            withdrawalService.saveWithdrawal(withdrawal);
            savingsAccount.setAccountBalance(
                    savingsAccount.getAccountBalance().subtract(withdrawalAmount)
            );
            savingsAccountService.saveSavingsAccount(savingsAccount);
        } catch (IllegalArgumentException e) {
            model.addAttribute(
                    "message",
                    "Could not make new withdrawal, "
                            + e.getMessage());
            return "error/error";
        }
        return "redirect:/user-withdrawals-current-month";
    }
}
