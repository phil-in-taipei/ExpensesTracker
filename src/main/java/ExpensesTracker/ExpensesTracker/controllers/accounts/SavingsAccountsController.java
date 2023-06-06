package ExpensesTracker.ExpensesTracker.controllers.accounts;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.accounts.forms.SavingsAccountCreateForm;
import ExpensesTracker.ExpensesTracker.models.accounts.forms.SavingsAccountUpdateForm;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.services.accounts.BankService;
import ExpensesTracker.ExpensesTracker.services.accounts.SavingsAccountService;
import ExpensesTracker.ExpensesTracker.services.currency.CurrencyService;
import ExpensesTracker.ExpensesTracker.services.users.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    CurrencyService currencyService;

    @Autowired
    SavingsAccountService savingsAccountService;

    @Autowired
    UserDetailsServiceImp userService;



    @GetMapping("/create-savings-account")
    public String showSubmitSavingsAccountPage(
            Authentication authentication, Model model) {
        //UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
        SavingsAccountCreateForm savingsAccount = new SavingsAccountCreateForm();
        //model.addAttribute("user", user);
        model.addAttribute("savingsAccount", savingsAccount);
        List<Bank> banks = bankService.getAllBanks();
        model.addAttribute("banks", banks);
        List<Currency> currencies = currencyService.getAllCurrencies();
        model.addAttribute("currencies", currencies);
        return "accounts/create-savings-account";
    }

    @GetMapping("/update-savings-account/{accountId}")
    public String showUpdateSavingsAccountPage(
            @PathVariable(name = "accountId")
            Long accountId,  Model model) {
        SavingsAccount accountToBeUpdated = savingsAccountService.getSavingsAccount(
                accountId);
        if (accountToBeUpdated == null) {
            model.addAttribute("message",
                    "Cannot update, savings account does not exist!");
            return "error/error";
        }
        SavingsAccountUpdateForm savingsAccount = new SavingsAccountUpdateForm();
        savingsAccount.setAccountBalance(accountToBeUpdated.getAccountBalance());
        savingsAccount.setAccountName(accountToBeUpdated.getAccountName());
        model.addAttribute("savingsAccount", savingsAccount);
        model.addAttribute("accountId", accountId);
        return "accounts/update-savings-account";
    }

    @RequestMapping("/delete-savings-account/{accountId}")
    public String deleteSavingsAccount(
            @PathVariable(name = "accountId")
            Long accountId, Model model) {
        if (savingsAccountService.getSavingsAccount(accountId) == null) {
            model.addAttribute("message",
                    "Cannot delete, savings account with id: "
                            + accountId + " does not exist.");
            return "error/error";
        }
        savingsAccountService.deleteSavingsAccount(accountId);
        return "redirect:/user-savings-accounts";
    }

    @PostMapping("/submit-savings-account")
    public String saveNewSavingsAccount(
            @ModelAttribute("savingsAccount")
            SavingsAccountCreateForm savingsAccountForm, Model model,
            Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
            Bank bank = bankService.getBank(savingsAccountForm.getBankId());
            Currency currency = currencyService.getCurrency(savingsAccountForm.getCurrencyId());
            SavingsAccount savingsAccount = new SavingsAccount();
            savingsAccount.setAccountBalance(BigDecimal.valueOf(0.00));
            savingsAccount.setBank(bank);
            savingsAccount.setCurrency(currency);
            savingsAccount.setAccountName(savingsAccountForm.getAccountName());
            savingsAccount.setUser(user);
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

    @PostMapping("/submit-updated-savings-account/{accountId}")
    public String updateSavingsAccount(
            @PathVariable(name = "accountId") Long accountId,
            @ModelAttribute("rescheduledTask")
            SavingsAccountUpdateForm savingsAccountUpdateForm,
            Model model) {
        SavingsAccount updatedSavingsAccount = savingsAccountService.getSavingsAccount(
                accountId);
        if (updatedSavingsAccount == null) {
            model.addAttribute("message",
                    "Cannot update, savings account does not exist!");
            return "error/error";
        } else {
            try {
                updatedSavingsAccount.setAccountName(savingsAccountUpdateForm.getAccountName());
                updatedSavingsAccount.setAccountBalance(savingsAccountUpdateForm.getAccountBalance());
                savingsAccountService.saveSavingsAccount(updatedSavingsAccount);
            } catch (IllegalArgumentException e) {
                model.addAttribute(
                        "message",
                        "Could not update savings account, "
                                + e.getMessage());
                return "error/error";
            }
        }
        return "redirect:/user-savings-accounts";
    }

    @GetMapping("/user-savings-accounts")
    public String showAllUsersAccounts(Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
        List<SavingsAccount> savingsAccounts = savingsAccountService
                .getAllAccountsByUserUsername(userDetails.getUsername());
        model.addAttribute("savingsAccounts", savingsAccounts);
        model.addAttribute("user", user);
        return "accounts/user-savings-accounts";
    }

}
