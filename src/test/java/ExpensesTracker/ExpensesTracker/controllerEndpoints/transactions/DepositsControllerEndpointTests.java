package ExpensesTracker.ExpensesTracker.controllerEndpoints.transactions;

import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.models.expenses.Expense;
import ExpensesTracker.ExpensesTracker.models.expenses.SpendingRecord;
import ExpensesTracker.ExpensesTracker.models.income.IncomeSource;
import ExpensesTracker.ExpensesTracker.models.transactions.Deposit;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.repositories.accounts.BankRepo;
import ExpensesTracker.ExpensesTracker.repositories.accounts.SavingAccountsRepo;
import ExpensesTracker.ExpensesTracker.repositories.currency.CurrencyRepo;
import ExpensesTracker.ExpensesTracker.repositories.income.IncomeSourceRepo;
import ExpensesTracker.ExpensesTracker.repositories.transactions.DepositRepo;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ExpensesTrackerApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class DepositsControllerEndpointTests {

    @Autowired
    BankRepo bankRepo;

    @Autowired
    CurrencyRepo currencyRepo;

    @Autowired
    DepositRepo depositRepo;

    @Autowired
    IncomeSourceRepo incomeSourceRepo;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SavingAccountsRepo savingAccountsRepo;

    @Autowired
    UserPrincipalRepo userPrincipalRepo;

    LocalDate today = LocalDate.now();
    Month month = today.getMonth();
    Year year = Year.of(today.getYear());

    LocalDate firstDayOfThisMonth = today.withDayOfMonth(1);
    LocalDate lastDayOfThisMonth = today.plusMonths(1)
            .withDayOfMonth(1).minusDays(1);

    @Test
    @Order(10)
    @WithUserDetails("Test Expenses Manager User1")
    public void testDeleteSpendingRecord() throws Exception {
        SavingsAccount testSavingsAccount = savingAccountsRepo.findAll().get(0);
        IncomeSource testIncomeSource = incomeSourceRepo.findAll().get(0);
        Deposit testDeposit = depositRepo.findAll().get(0);
        MockHttpServletRequestBuilder deleteDeposit = post(
                "/delete-deposit/" + testDeposit.getId());
        mockMvc.perform(deleteDeposit)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-deposits-current-month"));
        savingAccountsRepo.delete(testSavingsAccount);
        incomeSourceRepo.delete(testIncomeSource);
    }


    @Test
    @Order(1)
    @WithUserDetails("Test Expenses Manager User1")
    public void testSubmitNewDeposit()
            throws Exception {
        UserPrincipal testUser = userPrincipalRepo.findAll().get(0);
        Currency testCurrency = currencyRepo.findAll().get(0);
        IncomeSource testIncomeSource1 = incomeSourceRepo.save(IncomeSource.builder()
                .id(1L)
                .incomeSourceName("Test Income Source 1")
                .user(testUser)
                .build());
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
                post("/submit-deposit-form")
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
