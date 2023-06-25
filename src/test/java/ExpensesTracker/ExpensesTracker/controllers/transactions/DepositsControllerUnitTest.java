package ExpensesTracker.ExpensesTracker.controllers.transactions;

import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.controllers.expenses.SpendingRecordController;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.models.income.IncomeSource;
import ExpensesTracker.ExpensesTracker.models.transactions.Deposit;
import ExpensesTracker.ExpensesTracker.models.user.Authority;
import ExpensesTracker.ExpensesTracker.models.user.AuthorityEnum;
import ExpensesTracker.ExpensesTracker.models.user.UserMeta;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.repositories.currency.CurrencyRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.AuthorityRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.UserPrincipalRepo;
import ExpensesTracker.ExpensesTracker.services.accounts.SavingsAccountService;
import ExpensesTracker.ExpensesTracker.services.income.IncomeSourceService;
import ExpensesTracker.ExpensesTracker.services.transactions.DepositService;
import ExpensesTracker.ExpensesTracker.services.users.UserDetailsServiceImp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepositsController.class)
@ContextConfiguration(classes = {ExpensesTrackerApplication.class})
@ActiveProfiles("test")
public class DepositsControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthorityRepo authorityRepo;

    @MockBean
    CurrencyRepo currencyRepo;

    @MockBean
    DepositService depositService;

    @MockBean
    IncomeSourceService incomeSourceService;

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

    Currency testCurrency = Currency.builder()
            .id(1L)
            .currencyCode("TCC")
            .currencyName("Test Currency").build();

    LocalDate today = LocalDate.now();
    Month month = today.getMonth();
    Year year = Year.of(today.getYear());

    LocalDate firstDayOfThisMonth = today.withDayOfMonth(1);
    LocalDate lastDayOfThisMonth = today.plusMonths(1)
            .withDayOfMonth(1).minusDays(1);

    Bank testBank = Bank.builder()
            .id(1L)
            .bankName("Test Bank 1")
            .build();


    SavingsAccount testAccount1 = SavingsAccount.builder()
            .id(1L)
            .accountBalance(BigDecimal.valueOf(0.00))
            .accountName("Test Bank Account 1")
            .bank(testBank)
            .currency(testCurrency)
            .user(testUser)
            .build();

    IncomeSource testIncomeSource1 = IncomeSource.builder()
            .id(1L)
            .incomeSourceName("Test Income Source 1")
            .user(testUser)
            .build();

    Deposit testDeposit1 = Deposit.builder()
            .id(1L)
            .date(firstDayOfThisMonth.plusDays(1))
            .depositAmount(BigDecimal.valueOf(100.00))
            .incomeSource(testIncomeSource1)
            .savingsAccount(testAccount1)
            .build();

    Deposit testDeposit2 = Deposit.builder()
            .id(2L)
            .date(lastDayOfThisMonth.minusDays(1))
            .depositAmount(BigDecimal.valueOf(1200.00))
            .incomeSource(testIncomeSource1)
            .savingsAccount(testAccount1)
            .build();

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testSearchDepositsByMonthAndYear()
            throws Exception {
        List<Deposit> usersDeposits = new ArrayList<>();
        usersDeposits.add(testDeposit1);
        usersDeposits.add(testDeposit2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(depositService
                .getAllUserDepositsInDateRange(
                        anyString(), eq(firstDayOfThisMonth), eq(lastDayOfThisMonth)))
                .thenReturn(usersDeposits);
        MockHttpServletRequestBuilder submitSearch =
                post("/search-deposits-by-month-year")
                        .with(csrf())
                        .param("month", String.valueOf(month))
                        .param("year", "2023");
        mockMvc.perform(submitSearch)
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("deposits"))
                .andExpect(model().attributeExists("year"))
                .andExpect(model().attributeExists("month"))
                .andExpect(model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Date")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Account")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Amount")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Source")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Income Source 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Bank Account 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("100.0")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("1200.0")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("testuser")))
                .andExpect(view().name(
                        "transactions/user-deposits-by-month.html"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowAllUserDepositsForCurrentMonth()
            throws Exception {
        List<Deposit> usersDeposits = new ArrayList<>();
        usersDeposits.add(testDeposit1);
        usersDeposits.add(testDeposit2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(depositService
                .getAllUserDepositsInDateRange(
                        anyString(), eq(firstDayOfThisMonth), eq(lastDayOfThisMonth)))
                .thenReturn(usersDeposits);
        mockMvc.perform(get("/user-deposits-current-month"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("deposits"))
                .andExpect(model().attributeExists("year"))
                .andExpect(model().attributeExists("month"))
                .andExpect(model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Date")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Account")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Amount")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Source")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Income Source 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Bank Account 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("100.0")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("1200.0")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("testuser")))
                .andExpect(view().name(
                        "transactions/user-deposits-by-month.html"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testSearchDepositsByMonthAndYearPage() throws Exception {
        mockMvc
                .perform(get("/search-deposits-by-month-and-year"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("monthOptions"))
                .andExpect(model().attributeExists("searchMonthAndYear"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("2023")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("2023")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("JANUARY")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("FEBRUARY")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("MARCH")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("APRIL")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("MAY")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("JUNE")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("JULY")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("AUGUST")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("SEPTEMBER")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("OCTOBER")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("NOVEMBER")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("DECEMBER")))
                .andExpect(view().name(
                        "transactions/search-deposits-by-month-and-year.html"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowMakeDepositFormPage() throws Exception {
        List<SavingsAccount> usersSavingsAccounts = new ArrayList<>();
        usersSavingsAccounts.add(testAccount1);
        List<IncomeSource> usersIncomeSources = new ArrayList<>();
        usersIncomeSources.add(testIncomeSource1);
        when(savingsAccountService.getAllAccountsByUserUsername(anyString()))
                .thenReturn(usersSavingsAccounts);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(incomeSourceService.getAllIncomeSourcesByUserUsername(anyString()))
                .thenReturn(usersIncomeSources);
        mockMvc
                .perform(get("/make-deposit"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("savingsAccounts"))
                .andExpect(model().attributeExists("incomeSources"))
                .andExpect(model().attributeExists("deposit"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Make Deposit")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Date")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Amount")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Savings Account")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Income Source")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Income Source 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Bank Account 1")))
                .andExpect(view().name(
                        "transactions/make-deposit.html"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testSubmitNewDeposit() throws Exception {
        when(incomeSourceService.getIncomeSource(anyLong()))
                .thenReturn(testIncomeSource1);
        when(savingsAccountService.getSavingsAccount(anyLong()))
                .thenReturn(testAccount1);
        when(depositService.saveDeposit(any(Deposit.class)))
                .thenReturn(testDeposit1);
        when(savingsAccountService.saveSavingsAccount(any(SavingsAccount.class)))
                .thenReturn(testAccount1);
        MockHttpServletRequestBuilder makeDeposit =
                post("/submit-deposit-form")
                        .with(csrf())
                        .param("incomeSourceId", testIncomeSource1.getId().toString())
                        .param("date", firstDayOfThisMonth.toString())
                        .param("depositAmount", String.valueOf(200.00))
                        .param("savingsAccountId", testAccount1.getId().toString());
        mockMvc.perform(makeDeposit)
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-deposits-current-month"));
    }
}
