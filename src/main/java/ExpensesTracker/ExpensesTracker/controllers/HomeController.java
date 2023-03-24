package ExpensesTracker.ExpensesTracker.controllers;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {

    @GetMapping("/")
    public String homePage(Model model) {
        return "index";
    }

    @GetMapping("/landing")
    public String landingPage(Authentication authentication, Model model) {
        // get user info
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        model.addAttribute("user", user);
        return "landing";
    }
}
