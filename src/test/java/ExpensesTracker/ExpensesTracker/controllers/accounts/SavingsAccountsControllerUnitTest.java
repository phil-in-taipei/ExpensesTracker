package ExpensesTracker.ExpensesTracker.controllers.accounts;
import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.models.user.forms.UserRegistrationForm;
import ExpensesTracker.ExpensesTracker.repositories.user.AuthorityRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.UserPrincipalRepo;
import ExpensesTracker.ExpensesTracker.services.accounts.BankService;
import ExpensesTracker.ExpensesTracker.services.accounts.SavingsAccountService;
import ExpensesTracker.ExpensesTracker.services.users.UserDetailsServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SavingsAccountsController.class)
//@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {ExpensesTrackerApplication.class})
@ActiveProfiles("test")
public class SavingsAccountsControllerUnitTest {

    @Autowired
    MockMvc mockMvc;


    @MockBean
    AuthorityRepo authorityRepo;

    @MockBean
    BankService bankService;

    @MockBean
    SavingsAccountService savingsAccountService;

    @MockBean
    UserPrincipalRepo userPrincipalRepo;


    @MockBean
    UserDetailsServiceImp userService;

    @BeforeEach
    void init() {
        UserRegistrationForm userRegistration = new UserRegistrationForm();
        userRegistration.setSurname( "User");
        userRegistration.setGivenName( "Test");
        userRegistration.setEmail("test@gmx.com");
        userRegistration.setAge(40);
        userRegistration.setUsername("testuser");
        userRegistration.setPassword("testpassword");
        userRegistration.setPasswordConfirmation("testpassword");
        userService.createNewExpensesManagerUser(userRegistration);
    }

    @Test
    @WithMockUser(roles = {"USER", "EXPENSES_MANAGER"}, username = "testuser")
    public void testShowAllUsersAccounts() throws Exception {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        Bank testBank = Bank.builder()
                .id(1L)
                .bankName("Test Bank")
                .build();
        SavingsAccount testSavingsAccount1 = SavingsAccount.builder()
                .id(1L)
                .accountBalance(BigDecimal.valueOf(0.00))
                .bank(testBank)
                .user(testUser)
                .accountName("Test Savings Account 1")
                .build();
        SavingsAccount testSavingsAccount2 = SavingsAccount.builder()
                .id(1L)
                .accountBalance(BigDecimal.valueOf(0.00))
                .bank(testBank)
                .user(testUser)
                .accountName("Test Savings Account 2")
                .build();
        List<SavingsAccount> usersAccounts = new ArrayList<>();
        usersAccounts.add(testSavingsAccount1);
        usersAccounts.add(testSavingsAccount2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(savingsAccountService.getAllAccountsByUserUsername(anyString()))
                .thenReturn(usersAccounts);
        mockMvc
                .perform(get("/user-savings-accounts"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                    .contentType("text/html;charset=UTF-8"))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(model().attributeExists("savingsAccounts"))
                    .andExpect(MockMvcResultMatchers.content().string(
                            containsString("Test Savings Account 1")))
                    .andExpect(MockMvcResultMatchers.content().string(
                            containsString("Test Savings Account 2")))
                    .andExpect(view().name(
                            "accounts/user-savings-accounts"));
    }




}
