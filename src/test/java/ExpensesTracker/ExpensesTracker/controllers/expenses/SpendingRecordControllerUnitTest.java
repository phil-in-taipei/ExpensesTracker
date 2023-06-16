package ExpensesTracker.ExpensesTracker.controllers.expenses;

import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.models.expenses.Expense;
import ExpensesTracker.ExpensesTracker.models.expenses.SpendingRecord;
import ExpensesTracker.ExpensesTracker.models.user.Authority;
import ExpensesTracker.ExpensesTracker.models.user.AuthorityEnum;
import ExpensesTracker.ExpensesTracker.models.user.UserMeta;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.repositories.currency.CurrencyRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.AuthorityRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.UserPrincipalRepo;
import ExpensesTracker.ExpensesTracker.services.currency.CurrencyService;
import ExpensesTracker.ExpensesTracker.services.expenses.ExpenseService;
import ExpensesTracker.ExpensesTracker.services.expenses.SpendingRecordService;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpendingRecordController.class)
@ContextConfiguration(classes = {ExpensesTrackerApplication.class})
@ActiveProfiles("test")
public class SpendingRecordControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthorityRepo authorityRepo;

    @MockBean
    CurrencyRepo currencyRepo;

    @MockBean
    CurrencyService currencyService;

    @MockBean
    ExpenseService expenseService;

    @MockBean
    SpendingRecordService spendingRecordService;

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

    Expense testExpense1 = Expense.builder()
            .id(1L)
            .expenseName("Test Expense 1")
            .user(testUser)
            .build();

    Expense testExpense2 = Expense.builder()
            .id(1L)
            .expenseName("Test Expense 2")
            .user(testUser)
            .build();

    LocalDate today = LocalDate.now();
    Month month = today.getMonth();
    Year year = Year.of(today.getYear());

    LocalDate monthBegin = today.withDayOfMonth(1);
    LocalDate monthEnd = today.plusMonths(1)
            .withDayOfMonth(1).minusDays(1);

    SpendingRecord testSpendingRecord1 = SpendingRecord.builder()
            .id(1L)
            .expense(testExpense1)
            .amount(BigDecimal.valueOf(100.00))
            .currency(testCurrency)
            .date(monthBegin.plusDays(3))
            .build();

    SpendingRecord testSpendingRecord2 = SpendingRecord.builder()
            .id(2L)
            .expense(testExpense2)
            .amount(BigDecimal.valueOf(1200.00))
            .currency(testCurrency)
            .date(monthEnd.minusDays(5))
            .build();

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testSearchTasksByMonthAndYear()
            throws Exception {
        List<SpendingRecord> usersSpendingRecords = new ArrayList<>();
        usersSpendingRecords.add(testSpendingRecord1);
        usersSpendingRecords.add(testSpendingRecord2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(spendingRecordService
                .getAllUserSpendingRecordsInDateRange(
                        anyString(), eq(monthBegin), eq(monthEnd)))
                .thenReturn(usersSpendingRecords);
        MockHttpServletRequestBuilder submitSearch =
                post("/search-expenditures-by-month-year")
                        .with(csrf())
                        .param("month", String.valueOf(month))
                        .param("year", "2023");
        mockMvc.perform(submitSearch)
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("spendingRecords"))
                .andExpect(model().attributeExists("year"))
                .andExpect(model().attributeExists("month"))
                .andExpect(model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Date")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Amount")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Currency")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("TCC")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Expense 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("100.0")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Expense 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("1200.0")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("testuser")))
                .andExpect(view().name(
                        "expenses/user-spending-records-by-month"));
    }
    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowAllUserSpendingRecordsForCurrentMonth()
            throws Exception {
        List<SpendingRecord> usersSpendingRecords = new ArrayList<>();
        usersSpendingRecords.add(testSpendingRecord1);
        usersSpendingRecords.add(testSpendingRecord2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(spendingRecordService
                .getAllUserSpendingRecordsInDateRange(
                        anyString(), eq(monthBegin), eq(monthEnd)))
                .thenReturn(usersSpendingRecords);
        mockMvc
                .perform(get("/user-expenditures-current-month"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("spendingRecords"))
                .andExpect(model().attributeExists("year"))
                .andExpect(model().attributeExists("month"))
                .andExpect(model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Date")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Amount")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Currency")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("TCC")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Expense 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("100.0")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Expense 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("1200.0")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("testuser")))
                .andExpect(view().name(
                        "expenses/user-spending-records-by-month"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testSearchExpendituresByMonthAndYearPage() throws Exception {
        mockMvc
                .perform(get("/search-expenditures-by-month-and-year"))
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
                        "expenses/search-expenditures-by-month-and-year"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowRecordExpenditureFormPage() throws Exception {
        List<Expense> usersExpenses = new ArrayList<>();
        List<Currency> allCurrencies = new ArrayList<>();
        allCurrencies.add(testCurrency);
        usersExpenses.add(testExpense1);
        usersExpenses.add(testExpense2);
        when(currencyService.getAllCurrencies())
                .thenReturn(allCurrencies);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(expenseService.getAllExpensesByUserUsername(anyString()))
                .thenReturn(usersExpenses);
        mockMvc
                .perform(get("/record-expenditure"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("currencies"))
                .andExpect(model().attributeExists("expenses"))
                .andExpect(model().attributeExists("spendingRecord"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Date")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Amount")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Currency")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Currency")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Expense 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Expense 2")))
                .andExpect(view().name(
                        "expenses/record-expenditure"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testSubmitNewSpendingRecord() throws Exception {
        when(currencyService.getCurrency(anyLong()))
                .thenReturn(testCurrency);
        when(expenseService.getExpense(anyLong()))
                .thenReturn(testExpense1);
        MockHttpServletRequestBuilder createSpendingRecord =
                post("/submit-spending-record")
                        .with(csrf())
                        .param("expenseId", testExpense1.getId().toString())
                        .param("date", monthBegin.toString())
                        .param("amount", String.valueOf(200.00))
                        .param("currencyId", testCurrency.getId().toString());
        mockMvc.perform(createSpendingRecord)
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-expenditures-current-month"));
    }
}
