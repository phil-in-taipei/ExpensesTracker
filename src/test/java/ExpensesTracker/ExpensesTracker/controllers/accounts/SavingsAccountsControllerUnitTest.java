package ExpensesTracker.ExpensesTracker.controllers.accounts;
import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.models.user.Authority;
import ExpensesTracker.ExpensesTracker.models.user.AuthorityEnum;
import ExpensesTracker.ExpensesTracker.models.user.UserMeta;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.repositories.currency.CurrencyRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.AuthorityRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.UserPrincipalRepo;
import ExpensesTracker.ExpensesTracker.services.accounts.BankService;
import ExpensesTracker.ExpensesTracker.services.accounts.SavingsAccountService;
import ExpensesTracker.ExpensesTracker.services.currency.CurrencyService;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    CurrencyRepo currencyRepo;

    @MockBean
    CurrencyService currencyService;

    @MockBean
    SavingsAccountService savingsAccountService;

    @MockBean
    UserPrincipalRepo userPrincipalRepo;


    @MockBean
    UserDetailsServiceImp userService;

    UserMeta userMeta = UserMeta.builder()
            .id(1L)
            .email("testuser@gmx.com")
            .surname("Test")
            .givenName("User")
            .age(50)
            .build();
    Authority authority1 = Authority.builder().authority(AuthorityEnum.ROLE_USER).build();
    Authority authority2 = Authority.builder().authority(AuthorityEnum.ROLE_EXPENSES_MANAGER).build();
    List<Authority> authorities = Arrays.asList(authority1, authority2);
    UserPrincipal testUser = UserPrincipal.builder()
            .id(1L)
            .enabled(true)
            .credentialsNonExpired(true)
            .accountNonExpired(true)
            .accountNonLocked(true)
            .username("testuser")
            .authorities(authorities)
            .userMeta(userMeta)
            .password("testpassword")
            .build();

    Bank testBank1 = Bank.builder()
            .id(1L)
            .bankName("Test Bank 1")
            .build();
    Bank testBank2 = Bank.builder()
            .id(2L)
            .bankName("Test Bank 2")
            .build();

    Currency testCurrency = Currency.builder()
            .id(1L)
            .currencyCode("TCC")
            .currencyName("Test Currency").build();
    SavingsAccount testSavingsAccount1 = SavingsAccount.builder()
            .id(1L)
            .accountBalance(BigDecimal.valueOf(0.00))
            .bank(testBank1)
            .currency(testCurrency)
            .user(testUser)
            .accountName("Test Savings Account 1")
            .build();
    SavingsAccount testSavingsAccount2 = SavingsAccount.builder()
            .id(2L)
            .accountBalance(BigDecimal.valueOf(0.00))
            .bank(testBank2)
            .currency(testCurrency)
            .user(testUser)
            .accountName("Test Savings Account 2")
            .build();


    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testSaveNewSavingsAccount() throws Exception  {
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(bankService
                .getBank(anyLong()))
                .thenReturn(testBank1);
        when(currencyService
                .getCurrency(anyLong()))
                .thenReturn(testCurrency);
        when(savingsAccountService
                .saveSavingsAccount(any(SavingsAccount.class)))
                .thenReturn(testSavingsAccount1);
        MockHttpServletRequestBuilder createSavingsAccount =
                post("/submit-savings-account")
                        .with(csrf())
                        .param("accountName", "Test Savings Account 1")
                        .param("bankId", testBank1.getId().toString())
                        .param("currencyId", testCurrency.getId().toString());
        mockMvc.perform(createSavingsAccount)
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-savings-accounts"));
    }

    @Test
    @WithMockUser(roles = {"USER", "EXPENSES_MANAGER"}, username = "testuser")
    public void testShowAllUsersAccounts() throws Exception {
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

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowSubmitSavingsAccountPage() throws Exception {
        List<Bank> allBanks = new ArrayList<>();
        allBanks.add(testBank1);
        allBanks.add(testBank2);
        List<Currency> allCurrencies = new ArrayList<>();
        allCurrencies.add(testCurrency);
        when(bankService.getAllBanks())
                .thenReturn(allBanks);
        when(currencyService.getAllCurrencies())
                .thenReturn(allCurrencies);
        mockMvc
                .perform(get("/create-savings-account"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("savingsAccount"))
                .andExpect(model().attributeExists("currencies"))
                .andExpect(model().attributeExists("banks"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Bank 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Bank 2")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Currency")))
                .andExpect(view().name("accounts/create-savings-account"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowUpdateSavingsAccountPage() throws Exception {
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(savingsAccountService.getSavingsAccount(anyLong()))
                .thenReturn(testSavingsAccount1);
        MockHttpServletRequestBuilder updateAccount = get(
                "/update-savings-account/" + testSavingsAccount1.getId());
        mockMvc.perform(updateAccount)
                //.andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("savingsAccount"))
                .andExpect(model().attributeExists("accountId"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Savings Account 1")))
                .andExpect(view().name(
                        "accounts/update-savings-account"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testUpdateSavingsAccount() throws Exception {
        testSavingsAccount1.setAccountBalance(BigDecimal.valueOf(100.00));
        testSavingsAccount1.setAccountName("Updated Test Account");
        when(savingsAccountService.getSavingsAccount(anyLong()))
                .thenReturn(testSavingsAccount1);
        when(savingsAccountService
                .saveSavingsAccount(any(SavingsAccount.class)))
                .thenReturn(testSavingsAccount1);
        MockHttpServletRequestBuilder updateAccount = post(
                "/submit-updated-savings-account/" + testSavingsAccount1.getId())
                .with(csrf())
                .param("accountName", "Updated Test Account")
                .param("accountBalance", "100.00");
        mockMvc.perform(updateAccount)
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-savings-accounts"));
    }
}
