package ExpensesTracker.ExpensesTracker;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.user.Authority;
import ExpensesTracker.ExpensesTracker.models.user.AuthorityEnum;
import ExpensesTracker.ExpensesTracker.models.user.forms.UserRegistrationForm;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.repositories.accounts.BankRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.AuthorityRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.UserPrincipalRepo;
import ExpensesTracker.ExpensesTracker.services.users.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import javax.transaction.Transactional;

import java.util.Arrays;

@SpringBootApplication
@Profile("test")
public class ExpensesTrackerApplicationTest implements CommandLineRunner {

    @Autowired
    BankRepo bankRepo;

    @Autowired
    private UserPrincipalRepo userPrincipalRepo;

    @Autowired
    private UserDetailsServiceImp userService;

    public static void main(String[] args) {
        SpringApplication
                .run(ExpensesTrackerApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (bankRepo.findAll().isEmpty()) {
            Bank testBank = Bank.builder()
                    .id(1L)
                    .bankName("Test Bank 1")
                    .build();
            bankRepo.save(testBank);
        }
        if (userPrincipalRepo.findAll().isEmpty()) {
            System.out.println("The user repo is empty");
            UserRegistrationForm expensesManagerUserRegistration = new UserRegistrationForm();
            expensesManagerUserRegistration.setAge(20);
            expensesManagerUserRegistration.setEmail("testemail@gmx.com");
            expensesManagerUserRegistration.setPassword("testpassword");
            expensesManagerUserRegistration.setPasswordConfirmation("testpassword");
            expensesManagerUserRegistration.setGivenName("Test");
            expensesManagerUserRegistration.setSurname("User1");
            expensesManagerUserRegistration.setUsername("Test Expenses Manager User1");

            UserPrincipal expensesManagerUser1 = userService.createNewExpensesManagerUser(
                    expensesManagerUserRegistration
            );

            UserRegistrationForm adminUserRegistration = new UserRegistrationForm();
            adminUserRegistration.setAge(30);
            adminUserRegistration.setEmail("testemail@gmx.com");
            adminUserRegistration.setPassword("testpassword");
            adminUserRegistration.setPasswordConfirmation("testpassword");
            adminUserRegistration.setGivenName("Test");
            adminUserRegistration.setSurname("Admin User");
            adminUserRegistration.setUsername("Test Admin User1");

            UserPrincipal adminUser1 = userService.createNewAdminUser(
                    adminUserRegistration);
        }

    }
}

