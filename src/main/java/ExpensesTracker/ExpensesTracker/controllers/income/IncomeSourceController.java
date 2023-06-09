package ExpensesTracker.ExpensesTracker.controllers.income;

import ExpensesTracker.ExpensesTracker.models.income.IncomeSource;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.services.income.IncomeSourceService;
import ExpensesTracker.ExpensesTracker.services.users.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class IncomeSourceController {

    @Autowired
    IncomeSourceService incomeSourceService;

    @Autowired
    UserDetailsServiceImp userService;

    @GetMapping("/create-income-source")
    public String showCreateIncomeSourcePage(
            Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
        IncomeSource incomeSource = new IncomeSource();
        model.addAttribute("user", user);
        model.addAttribute("incomeSource", incomeSource);
        return "income/create-income-source";
    }

    @RequestMapping("/delete-income-source/{incomeSourceId}")
    public String deleteSavingsAccount(
            @PathVariable(name = "incomeSourceId")
            Long incomeSourceId, Model model) {
        if (incomeSourceService.getIncomeSource(incomeSourceId) == null) {
            model.addAttribute("message",
                    "Cannot delete, income source with id: "
                            + incomeSourceId + " does not exist.");
            return "error/error";
        }
        incomeSourceService.deleteIncomeSource(incomeSourceId);
        return "redirect:/user-income-sources";
    }

    @PostMapping("/submit-income-source")
    public String saveNewIncomeSource(
        @ModelAttribute("incomeSource")
        IncomeSource incomeSource, Model model, Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
            incomeSource.setUser(user);
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
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
        List<IncomeSource> incomeSources = incomeSourceService
                .getAllIncomeSourcesByUserUsername(user.getUsername());
        model.addAttribute("incomeSources", incomeSources);
        return "income/user-income-sources";
    }
}
