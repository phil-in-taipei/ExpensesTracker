package ExpensesTracker.ExpensesTracker.controllers.transactions;

import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.search.SearchMonthAndYearForm;
import ExpensesTracker.ExpensesTracker.models.transactions.Deposit;
import ExpensesTracker.ExpensesTracker.models.transactions.TransactionRecord;
import ExpensesTracker.ExpensesTracker.models.transactions.forms.SearchAccountActivityByMonthAndYearForm;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.services.accounts.SavingsAccountService;
import ExpensesTracker.ExpensesTracker.services.transactions.DepositService;
import ExpensesTracker.ExpensesTracker.services.transactions.TransactionsService;
import ExpensesTracker.ExpensesTracker.services.transactions.WithdrawalService;
import ExpensesTracker.ExpensesTracker.services.users.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;

@Controller
public class TransactionsController {

    @Autowired
    SavingsAccountService savingsAccountService;

    @Autowired
    TransactionsService transactionsService;

    @Autowired
    UserDetailsServiceImp userService;


    @PostMapping("/search-account-transactions-by-month-and-year")
    public String showAllAccountTransactionsByMonthAndYear(
            @ModelAttribute("searchAccountMonthAndYear")
            SearchAccountActivityByMonthAndYearForm searchAccountMonthAndYear,
            Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
        Month month = searchAccountMonthAndYear.getMonth();
        System.out.println("This is the month in the query: " + month);
        int queryMonth = month.getValue();
        int queryYear = searchAccountMonthAndYear.getYear();
        Long accountId = searchAccountMonthAndYear.getSavingsAccountId();
        SavingsAccount savingsAccount = savingsAccountService.getSavingsAccount(accountId);
        System.out.println("********This is the savings account: " + savingsAccount.templateSelector() + "***********");
        LocalDate date = LocalDate.now();
        // finds local date for the first day of the specified month/year
        LocalDate monthBegin = date.withDayOfMonth(1).withMonth(queryMonth)
                .withYear(queryYear);
        System.out.println("This is the first date of the month: " + monthBegin);
        // finds local date for the last day of the specified month/year
        LocalDate monthEnd = monthBegin.plusMonths(1)
                .withDayOfMonth(1).minusDays(1);
        System.out.println("This is the last date: " + monthEnd);
        List<TransactionRecord> transactionRecords = transactionsService
                .getAllTransactionsBySavingsAccountInDateRange(
                accountId, monthBegin, monthEnd);
        model.addAttribute("year", queryYear);
        model.addAttribute("month", month);
        model.addAttribute("savingsAccount", savingsAccount);
        model.addAttribute("transactionRecords", transactionRecords);
        model.addAttribute("user", user);
        return "transactions/account-transactions-by-month.html";
    }

    @GetMapping("/search-account-transactions-by-month-and-year")
    public String showSearchAccountTransactionsByMonthAndYearPage(
            Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
        List<SavingsAccount> savingsAccounts = savingsAccountService
                .getAllAccountsByUserUsername(userDetails.getUsername());
        model.addAttribute("savingsAccounts", savingsAccounts);
        model.addAttribute("user", user);
        Month[] monthOptions = Month.values();
        model.addAttribute("monthOptions", monthOptions);
        model.addAttribute("searchAccountMonthAndYear",
                new SearchAccountActivityByMonthAndYearForm());
        return "transactions/search-account-transactions-by-month-and-year.html";
    }
}
