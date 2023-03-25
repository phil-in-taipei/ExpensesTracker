package ExpensesTracker.ExpensesTracker.controllers.accounts;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.services.accounts.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BanksController {

    @Autowired
    BankService bankService;

    @GetMapping("/banks")
    public String showAllBanks(Model model) {
        List<Bank> banks = bankService.getAllBanks();
        model.addAttribute("banks", banks);
        return "accounts/banks";
    }

    @GetMapping("/create-bank")
    public String showSubmitBankPage(Model model) {
        Bank bank = new Bank();
        model.addAttribute("bank", bank);
        return "accounts/create-bank";
    }

    @PostMapping("/submit-bank")
    public String saveNewBank(
            @ModelAttribute("bank")
            Bank bank, Model model) {
        try {
            bankService.saveBank(bank);
        } catch (IllegalArgumentException e) {
            model.addAttribute(
                    "message",
                    "Could not save bank, "
                            + e.getMessage());
            return "error/error";
        }
        return "redirect:/banks";
    }
}
