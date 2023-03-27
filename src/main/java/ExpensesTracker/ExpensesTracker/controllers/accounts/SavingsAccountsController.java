package ExpensesTracker.ExpensesTracker.controllers.accounts;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.services.accounts.BankService;
import ExpensesTracker.ExpensesTracker.services.accounts.SavingsAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class SavingsAccountsController {

    @Autowired
    BankService bankService;

    @Autowired
    SavingsAccountService savingsAccountService;

    @GetMapping("/create-savings-account")
    public String showSubmitSavingsAccountPage(
            Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        SavingsAccount savingsAccount = new SavingsAccount();
        model.addAttribute("user", user);
        model.addAttribute("savingsAccount", savingsAccount);
        List<Bank> banks = bankService.getAllBanks();
        model.addAttribute("banks", banks);

        return "accounts/create-savings-account";
    }

    @PostMapping("/submit-savings-account")
    public String saveNewSavingsAccount(
            @ModelAttribute("savingsAccount")
            SavingsAccount savingsAccount, Model model) {
        try {
            savingsAccount.setAccountBalance(BigDecimal.valueOf(0.00));
            savingsAccountService.saveSavingsAccount(savingsAccount);
        } catch (IllegalArgumentException e) {
            model.addAttribute(
                    "message",
                    "Could not save account, "
                            + e.getMessage());
            return "error/error";
        }
        return "redirect:/user-savings-accounts";
    }

    @GetMapping("/user-savings-accounts")
    public String showAllUsersAccounts(Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        List<SavingsAccount> savingsAccounts = savingsAccountService
                .getAllAccountsByUserId(user.getId());
        model.addAttribute("savingsAccounts", savingsAccounts);
        return "accounts/user-savings-accounts";
    }

}
