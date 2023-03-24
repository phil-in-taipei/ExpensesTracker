package ExpensesTracker.ExpensesTracker.controllers.users;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.services.users.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class UsersController {

    @Autowired
    UserDetailsServiceImp userDetailsService;

    @RequestMapping("/delete-user/{id}")
    public String deleteCustomer(@PathVariable(name = "id") Long id, Model model) {
        if (userDetailsService.getUserById(id) == null) {
            model.addAttribute("message",
                    "Cannot delete, customer with id " + id + " does not exist.");
            return "error";
        }
        userDetailsService.deleteUserPrincipal(id);
        return "redirect:/expenses-tracker-users";
    }

    @GetMapping("/expenses-tracker-users")
    public String showAllExpensesManagerUsersPage(Model model) {
        List<UserPrincipal> expensesManagerUsers = userDetailsService.getAllExpensesManagers();
        model.addAttribute("expensesManagerUsers", expensesManagerUsers);
        return "users/expenses-manager-users";
    }
}
