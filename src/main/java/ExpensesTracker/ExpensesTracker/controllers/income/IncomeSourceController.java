package ExpensesTracker.ExpensesTracker.controllers.income;

import ExpensesTracker.ExpensesTracker.models.income.IncomeSource;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.services.income.IncomeSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class IncomeSourceController {

    @Autowired
    IncomeSourceService incomeSourceService;

    @GetMapping("/create-income-source")
    public String showSubmitIncomeSourcePage(
            Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        IncomeSource incomeSource = new IncomeSource();
        model.addAttribute("user", user);
        model.addAttribute("incomeSource", incomeSource);
        return "income/create-income-source";
    }

    @PostMapping("/submit-income-source")
    public String saveNewSavingsAccount(
        @ModelAttribute("incomeSource")
        IncomeSource incomeSource, Model model) {
        try {
            incomeSourceService.saveIncomeSource(incomeSource);
        } catch (IllegalArgumentException e) {
            model.addAttribute(
                    "message",
                    "Could not save income source, "
                            + e.getMessage());
            return "error/error";
        }
        return "redirect:/user-income-sources";
    }

    @GetMapping("/user-income-sources")
    public String showAllUsersIncomeSources(
            Authentication authentication, Model model){
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        List<IncomeSource> incomeSources = incomeSourceService
                .getAllIncomeSourcesByUserId(user.getId());
        model.addAttribute("incomeSources", incomeSources);
        return "income/user-income-sources";
    }
}
