package ExpensesTracker.ExpensesTracker.controllers.users;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.services.users.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UsersController {

    @Autowired
    UserDetailsServiceImp userDetailsService;

    @GetMapping("/expenses-tracker-users")
    public String showAllExpensesManagerUsersPage(Model model) {
        List<UserPrincipal> expensesManagerUsers = userDetailsService.getAllExpensesManagers();
        model.addAttribute("expensesManagerUsers", expensesManagerUsers);
        return "users/expenses-manager-users";
    }
}
