package ExpensesTracker.ExpensesTracker.controllers.auth;
import ExpensesTracker.ExpensesTracker.logging.Loggable;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.models.user.forms.UserRegistrationForm;
import ExpensesTracker.ExpensesTracker.services.users.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Objects;

@Controller
public class RegistrationController {

    @Autowired
    UserDetailsServiceImp userDetailsService;

    @Loggable
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userRegistrationForm", new UserRegistrationForm());
        return "auth/register";
    }

    @Loggable
    @PostMapping("/register")
    public String submitRegisterForm(
            @ModelAttribute("userRegistration")
            UserRegistrationForm userRegistration,
            Model model){
        // verifies that the passwords match prior to registration;
        // otherwise, redirects to registration failure page
        if (!userDetailsService.confirmPasswordsMatch(userRegistration)) {
            model.addAttribute("errorMsg", "The passwords do not match!");
            return "auth/register-failure";
        }
        // verifies that a user with the username doesn't already exist
        // prior to registration; otherwise, redirects to registration failure page
        if (userDetailsService.usernameAlreadyExists(userRegistration.getUsername())) {
            model.addAttribute("errorMsg",
                    "The username, " +
                            userRegistration.getUsername() +
                            ", has already been taken. Please select another username");
            return "auth/register-failure";
        }
        UserPrincipal createdUser = userDetailsService.createNewExpensesManagerUser(userRegistration);
        model.addAttribute("user", createdUser);
        return "auth/register-success";
    }

    @Loggable
    @GetMapping("/register-admin")
    public String showRegisterFormForAdmin(Model model) {
        model.addAttribute("userRegistrationForm", new UserRegistrationForm());
        return "auth/register-admin";
    }

    @Loggable
    @PostMapping("/register-admin")
    public String submitRegisterFormForAdmin(
            @ModelAttribute("userRegistration") UserRegistrationForm userRegistration,
            Model model) throws
            SQLIntegrityConstraintViolationException {
        // verifies that the passwords match prior to registration;
        // otherwise, redirects to registration failure page
        if (!Objects.equals(userRegistration.getPassword(),
                userRegistration.getPasswordConfirmation())) {
            model.addAttribute("errorMsg",
                    "The passwords do not match!");
            return "auth/register-failure";
        }
        // check if the username has already been used, if so return an error
        if (userDetailsService.loadUserByUsername(userRegistration.getUsername())!=null) {
            model.addAttribute("errorMsg",
                    "That username is unavailable! Please try again");
            return "auth/register-failure";
        }
        UserPrincipal createdUser = userDetailsService
                .createNewAdminUser(userRegistration);
        // verifies user creation successful; otherwise, redirects to registration failure page
        if (createdUser == null) {
            model.addAttribute("errorMsg",
                    "There was error creating your account");
            return "register-failure";
        }
        model.addAttribute("user", createdUser);
        return "auth/register-success";
    }
}
