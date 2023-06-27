package ExpensesTracker.ExpensesTracker.controllerEndpoints.transactions;

import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.models.transactions.Withdrawal;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.repositories.accounts.BankRepo;
import ExpensesTracker.ExpensesTracker.repositories.accounts.SavingAccountsRepo;
import ExpensesTracker.ExpensesTracker.repositories.currency.CurrencyRepo;
import ExpensesTracker.ExpensesTracker.repositories.transactions.WithdrawalRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.UserPrincipalRepo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ExpensesTrackerApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class WithdrawalsControllerEndpointTests {

    @Autowired
    BankRepo bankRepo;

    @Autowired
    CurrencyRepo currencyRepo;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SavingAccountsRepo savingAccountsRepo;

    @Autowired
    UserPrincipalRepo userPrincipalRepo;

    @Autowired
    WithdrawalRepo withdrawalRepo;

    LocalDate today = LocalDate.now();
    Month month = today.getMonth();
    Year year = Year.of(today.getYear());

    LocalDate firstDayOfThisMonth = today.withDayOfMonth(1);
    LocalDate lastDayOfThisMonth = today.plusMonths(1)
            .withDayOfMonth(1).minusDays(1);

    @Test
    @Order(10)
    @WithUserDetails("Test Expenses Manager User1")
    public void testDeleteWithdrawal() throws Exception {
        SavingsAccount testSavingsAccount = savingAccountsRepo.findAll().get(0);
        Withdrawal testWithdrawal = withdrawalRepo.findAll().get(0);
        MockHttpServletRequestBuilder deleteWithdrawal = post(
                "/delete-withdrawal/" + testWithdrawal.getId());
        mockMvc.perform(deleteWithdrawal)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-withdrawals-current-month"));
        savingAccountsRepo.delete(testSavingsAccount);
    }

    @Test
    @Order(4)
    @WithUserDetails("Test Expenses Manager User1")
    public void testSearchWithdrawalsByMonthAndYear()
            throws Exception {
        MockHttpServletRequestBuilder submitSearch =
                post("/search-withdrawals-by-month-year")
                        .param("month", String.valueOf(month))
                        .param("year", "2023");
        mockMvc.perform(submitSearch)
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("withdrawals"))
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
                        containsString("Test Bank Account 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("200.0")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Expenses Manager User1")))
                .andExpect(view().name(
                        "transactions/user-withdrawals-by-month.html"));
    }

    @Test
    @Order(2)
    @WithUserDetails("Test Expenses Manager User1")
    public void testSearchWithdrawalsByMonthAndYearPage() throws Exception {
        mockMvc
                .perform(get("/search-withdrawals-by-month-and-year"))
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
                        "transactions/search-withdrawals-by-month-and-year.html"));
    }

    @Test
    @Order(5)
    @WithUserDetails("Test Expenses Manager User1")
    public void testShowAllUserWithdrawalsForCurrentMonth()
            throws Exception {
        mockMvc.perform(get("/user-withdrawals-current-month"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("withdrawals"))
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
                        containsString("Test Bank Account 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("200.0")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Expenses Manager User1")))
                .andExpect(view().name(
                        "transactions/user-withdrawals-by-month.html"));
    }

    @Test
    @Order(3)
    @WithUserDetails("Test Expenses Manager User1")
    public void testShowMakeWithdrawalFormPage() throws Exception {
        mockMvc
                .perform(get("/make-withdrawal"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("savingsAccounts"))
                .andExpect(model().attributeExists("withdrawal"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Make Withdrawal")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Date")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Amount")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Savings Account")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Bank Account 1")))
                .andExpect(view().name(
                        "transactions/make-withdrawal.html"));
    }


    @Test
    @Order(1)
    @WithUserDetails("Test Expenses Manager User1")
    public void testSubmitNewWithdrawal()
            throws Exception {
        UserPrincipal testUser = userPrincipalRepo.findAll().get(0);
        Currency testCurrency = currencyRepo.findAll().get(0);
        Bank testBank = bankRepo.findAll().get(0);
        SavingsAccount testAccount1 = savingAccountsRepo.save(
                SavingsAccount.builder()
                        .id(1L)
                        .accountBalance(BigDecimal.valueOf(0.00))
                        .accountName("Test Bank Account 1")
                        .bank(testBank)
                        .currency(testCurrency)
                        .user(testUser)
                        .build());
        MockHttpServletRequestBuilder makeDeposit =
                post("/submit-withdrawal-form")
                        .param("date", firstDayOfThisMonth.toString())
                        .param("amount", String.valueOf(200.00))
                        .param("savingsAccountId", testAccount1.getId().toString());
        mockMvc.perform(makeDeposit)
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-withdrawals-current-month"));
    }
}
