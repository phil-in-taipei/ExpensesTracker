package ExpensesTracker.ExpensesTracker;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.models.user.Authority;
import ExpensesTracker.ExpensesTracker.models.user.AuthorityEnum;
import ExpensesTracker.ExpensesTracker.models.user.forms.UserRegistrationForm;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.repositories.accounts.BankRepo;
import ExpensesTracker.ExpensesTracker.repositories.currency.CurrencyRepo;
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
    private AuthorityRepo authorityRepo;

    @Autowired
    BankRepo bankRepo;

    @Autowired
    CurrencyRepo currencyRepo;

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
        System.out.print("*****************Running Tests**********************");
        if (bankRepo.findAll().isEmpty()) {
            Bank testBank = Bank.builder()
                    .id(1L)
                    .bankName("Test Bank 1")
                    .build();
            bankRepo.save(testBank);
            System.out.print("*********Created Bank: " + testBank.toString() + "**************");
        }

        if (currencyRepo.findAll().isEmpty()) {
            Currency testCurrency = Currency.builder()
                    .id(1L)
                    .currencyCode("TCC")
                    .currencyName("Test Currency").build();
            currencyRepo.save(testCurrency);
            System.out.print("*********Created Currency: " + testCurrency.toString() + "**************");
        }

        if (authorityRepo.findAll().isEmpty()) {
            // constructs the Authorities for the different user types and
            // saves to database. This is a key step before creating any users
            Authority userAuth = Authority.builder().id(1L)
                    .authority(AuthorityEnum.ROLE_USER).build();
            System.out.print("*********Created Authority: " + userAuth.toString() + "**************");
            Authority adminAuth = Authority.builder().id(2L)
                    .authority(AuthorityEnum.ROLE_ADMIN).build();
            System.out.print("*********Created Authority: " + adminAuth.toString() + "**************");
            Authority mainAuth = Authority.builder().id(3L)
                    .authority(AuthorityEnum.ROLE_EXPENSES_MANAGER).build();
            System.out.print("*********Created Authority: " + mainAuth.toString() + "**************");
            authorityRepo.saveAll(Arrays.asList(userAuth, adminAuth, mainAuth));
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

            System.out.print("*********Created Regular User: "
                    + expensesManagerUser1.toString() + "**************");

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
            System.out.print("*********Created Admin User: "
                    + adminUser1.toString() + "**************");
        }
        //userService.deleteUserPrincipalByUsername("Test Expenses Manager User2");
    }
}

