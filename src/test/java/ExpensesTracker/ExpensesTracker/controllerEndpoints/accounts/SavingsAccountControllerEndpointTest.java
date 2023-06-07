package ExpensesTracker.ExpensesTracker.controllerEndpoints.accounts;
import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.repositories.accounts.BankRepo;
import ExpensesTracker.ExpensesTracker.repositories.accounts.SavingAccountsRepo;
import ExpensesTracker.ExpensesTracker.repositories.currency.CurrencyRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.UserPrincipalRepo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest(classes = ExpensesTrackerApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class SavingsAccountControllerEndpointTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    BankRepo bankRepo;

    @Autowired
    CurrencyRepo currencyRepo;

    @Autowired
    SavingAccountsRepo savingsAccountsRepo;

    @Autowired
    UserPrincipalRepo userPrincipalRepo;

    @Test
    @Order(10)
    @WithUserDetails("Test Expenses Manager User1")
    public void testDeleteSavingsAccount() throws Exception {
        SavingsAccount testAccount = savingsAccountsRepo.findAll().get(0);
        MockHttpServletRequestBuilder deleteAccount = post(
                "/delete-savings-account/" + testAccount.getId());
        mockMvc.perform(deleteAccount)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-savings-accounts"));
    }

    @Test
    @Order(11)
    @WithUserDetails("Test Expenses Manager User1")
    public void testDeleteSavingsAccountFailure() throws Exception {
        Long nonSavingsAccountID = 2829L;
        String message = "Cannot delete, savings account with id: 2829" +
                " does not exist.";
        MockHttpServletRequestBuilder deleteAccount = post(
                "/delete-savings-account/" + nonSavingsAccountID);
        mockMvc.perform(deleteAccount)
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(message)))
                .andExpect(view().name("error/error"));
    }

    @Test
    @Order(1)
    @WithUserDetails("Test Expenses Manager User1")
    public void testSaveNewSavingsAccount()
            throws Exception {
        Bank testBank = bankRepo.findAll().get(0);
        Currency testCurrency = currencyRepo.findAll().get(0);
        UserPrincipal testUser = userPrincipalRepo.findAll().get(0);
        MockHttpServletRequestBuilder createAccount = post(
                "/submit-savings-account")
                .param("bankId", String.valueOf(testBank.getId()))
                .param("currencyId", String.valueOf(testCurrency.getId()))
                .param("accountName", "Test Savings Account");
        mockMvc.perform(createAccount)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-savings-accounts"));
    }

    @Test
    @Order(2)
    @WithUserDetails("Test Expenses Manager User1")
    public void testShowAllUsersAccounts() throws Exception {
        mockMvc
                .perform(get("/user-savings-accounts"))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("savingsAccounts"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Savings Account")))
                .andExpect(view().name(
                        "accounts/user-savings-accounts"));
    }

    @Test
    @Order(5)
    @WithUserDetails("Test Expenses Manager User1")
    public void testShowSubmitSavingsAccountPage() throws Exception {
        mockMvc
                .perform(get("/create-savings-account"))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("savingsAccount"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Bank 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Euro Member Countries")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Taiwan New Dollar")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("United States Dollar")))
                .andExpect(view().name(
                        "accounts/create-savings-account"));
    }

    @Test
    @Order(3)
    @WithUserDetails("Test Expenses Manager User1")
    public void testShowUpdateSavingsAccountPage() throws Exception {
        SavingsAccount testAccount = savingsAccountsRepo.findAll().get(0);
        MockHttpServletRequestBuilder updateAccount = get(
                "/update-savings-account/" + testAccount.getId());
        mockMvc.perform(updateAccount)
                //.andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("savingsAccount"))
                .andExpect(model().attributeExists("accountId"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Savings Account")))
                .andExpect(view().name(
                        "accounts/update-savings-account"));
    }

    @Test
    @Order(4)
    @WithUserDetails("Test Expenses Manager User1")
    public void testUpdateSavingsAccount() throws Exception {
        SavingsAccount testAccount = savingsAccountsRepo.findAll().get(0);
        MockHttpServletRequestBuilder updateAccount = post(
                "/submit-updated-savings-account/" + testAccount.getId())
                .param("accountName", "Updated Test Account")
                .param("accountBalance", "100.00");
        mockMvc.perform(updateAccount)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-savings-accounts"));
    }
}
