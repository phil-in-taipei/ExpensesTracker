package ExpensesTracker.ExpensesTracker.controllers.transactions;

import ExpensesTracker.ExpensesTracker.services.accounts.SavingsAccountService;
import ExpensesTracker.ExpensesTracker.services.income.IncomeSourceService;
import ExpensesTracker.ExpensesTracker.services.transactions.DepositService;
import ExpensesTracker.ExpensesTracker.services.users.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class DepositsController {

    @Autowired
    DepositService depositService;

    @Autowired
    IncomeSourceService incomeSourceService;

    @Autowired
    SavingsAccountService savingsAccountService;

    @Autowired
    UserDetailsServiceImp userService;
}
