package ExpensesTracker.ExpensesTracker.controllers.transactions;

import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.income.IncomeSource;
import ExpensesTracker.ExpensesTracker.models.search.SearchMonthAndYearForm;
import ExpensesTracker.ExpensesTracker.models.transactions.Deposit;
import ExpensesTracker.ExpensesTracker.models.transactions.forms.DepositForm;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.services.accounts.SavingsAccountService;
import ExpensesTracker.ExpensesTracker.services.income.IncomeSourceService;
import ExpensesTracker.ExpensesTracker.services.transactions.DepositService;
import ExpensesTracker.ExpensesTracker.services.users.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;

@Controller
public class DepositsController {

    @Autowired
    DepositService depositService;

    @Autowired
    IncomeSourceService incomeSourceService;

    @Autowired
    SavingsAccountService savingsAccountService;

    @Autowired
    UserDetailsServiceImp userService;

    // currently this is just for testing
    @RequestMapping("/delete-deposit/{depositId}")
    public String deleteDeposit(
            @PathVariable(name = "depositId")
            Long depositId, Model model) {
        Deposit depositToBeDeleted = depositService.getDeposit(depositId);
        if (depositToBeDeleted == null) {
            model.addAttribute("message",
                    "Cannot delete, deposit with id: "
                            + depositId + " does not exist.");
            return "error/error";
        }
        // the amount of money in the deleted deposit object
        // will have to be added back into the account balance
        // in the related savings account and saved
        Long savingsAccountId = depositToBeDeleted.getSavingsAccount().getId();
        BigDecimal depositAmount = depositToBeDeleted.getDepositAmount();
        depositService.deleteDeposit(depositId);
        SavingsAccount savingsAccount = savingsAccountService.getSavingsAccount(savingsAccountId);
        savingsAccount.setAccountBalance(
                savingsAccount.getAccountBalance().subtract(depositAmount));
        savingsAccountService.saveSavingsAccount(savingsAccount);
        return "redirect:/user-deposits-current-month";
    }

    @GetMapping("/make-deposit")
    public String showMakeDepositFormPage(
            Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        DepositForm depositForm = new DepositForm();
        model.addAttribute("deposit", depositForm);
        List<IncomeSource> incomeSources = incomeSourceService
                .getAllIncomeSourcesByUserUsername(userDetails.getUsername());
        model.addAttribute("incomeSources", incomeSources);
        List<SavingsAccount> savingsAccounts = savingsAccountService
                .getAllAccountsByUserUsername(userDetails.getUsername());
        model.addAttribute("savingsAccounts", savingsAccounts);
        return "transactions/make-deposit.html";
    }

    @PostMapping("/search-deposits-by-month-year")
    public String searchDepositsByMonthAndYear(
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
        List<Deposit> deposits = depositService
                .getAllUserDepositsInDateRange(user.getUsername(), monthBegin, monthEnd);
        model.addAttribute("year", queryYear);
        model.addAttribute("month", month);
        model.addAttribute("deposits", deposits);
        model.addAttribute("user", user);
        return "transactions/user-deposits-by-month.html";
    }

    @GetMapping("/search-deposits-by-month-and-year")
    public String showSearchDepositsByMonthAndYearPage(Model model) {
        Month[] monthOptions = Month.values();
        model.addAttribute("monthOptions", monthOptions);
        model.addAttribute("searchMonthAndYear",
                new SearchMonthAndYearForm());
        return "transactions/search-deposits-by-month-and-year.html";
    }

    @PostMapping("/submit-deposit-form")
    public String submitNewDeposit(
            @ModelAttribute("deposit")
            DepositForm depositForm,
            Model model, Authentication authentication) {
        try {
            BigDecimal depositAmount = depositForm.getDepositAmount();
            SavingsAccount savingsAccount = savingsAccountService
                    .getSavingsAccount(depositForm.getSavingsAccountId());
            IncomeSource incomeSource = incomeSourceService
                    .getIncomeSource(depositForm.getIncomeSourceId());
            LocalDate date = LocalDate.parse(depositForm.getDate());
            Deposit deposit = new Deposit();
            deposit.setDepositAmount(depositAmount);
            deposit.setSavingsAccount(savingsAccount);
            deposit.setDate(date);
            deposit.setIncomeSource(incomeSource);
            depositService.saveDeposit(deposit);
            savingsAccount.setAccountBalance(
                    savingsAccount.getAccountBalance().add(depositAmount)
            );
            savingsAccountService.saveSavingsAccount(savingsAccount);
        } catch (IllegalArgumentException e) {
            model.addAttribute(
                    "message",
                    "Could not make new deposit, "
                            + e.getMessage());
            return "error/error";
        }
        return "redirect:/user-deposits-current-month";
    }

    @GetMapping("/user-deposits-current-month")
    public String showAllUserDepositsForCurrentMonth(
            Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
        LocalDate today = LocalDate.now();
        Month month = today.getMonth();
        Year year = Year.of(today.getYear());
        LocalDate monthBegin = today.withDayOfMonth(1);
        LocalDate monthEnd = today.plusMonths(1)
                .withDayOfMonth(1).minusDays(1);
        List<Deposit> deposits = depositService.getAllUserDepositsInDateRange(
                user.getUsername(), monthBegin, monthEnd);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("deposits", deposits);
        model.addAttribute("user", user);
        return "transactions/user-deposits-by-month.html";
    }
}
