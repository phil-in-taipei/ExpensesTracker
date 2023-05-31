package ExpensesTracker.ExpensesTracker.controllers;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.services.users.UserDetailsServiceImp;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    UserDetailsServiceImp userService;

    @GetMapping("/")
    public String homePage(Model model) {
        return "index";
    }

    @GetMapping("/landing")
    public String landingPage(Authentication authentication, Model model) {
        // get user info
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        return "landing";
    }
}
