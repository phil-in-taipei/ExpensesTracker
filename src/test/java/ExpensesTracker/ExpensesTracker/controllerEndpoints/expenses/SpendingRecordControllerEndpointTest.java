package ExpensesTracker.ExpensesTracker.controllerEndpoints.expenses;
import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.models.expenses.Expense;
import ExpensesTracker.ExpensesTracker.models.expenses.SpendingRecord;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.repositories.currency.CurrencyRepo;
import ExpensesTracker.ExpensesTracker.repositories.expenses.ExpenseRepo;
import ExpensesTracker.ExpensesTracker.repositories.expenses.SpendingRecordRepo;
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

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ExpensesTrackerApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class SpendingRecordControllerEndpointTest {

    @Autowired
    CurrencyRepo currencyRepo;

    @Autowired
    ExpenseRepo expenseRepo;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SpendingRecordRepo spendingRecordRepo;

    @Autowired
    UserPrincipalRepo userPrincipalRepo;

    LocalDate today = LocalDate.now();
    Month month = today.getMonth();
    Year year = Year.of(today.getYear());

    LocalDate monthBegin = today.withDayOfMonth(1);
    LocalDate monthEnd = today.plusMonths(1)
            .withDayOfMonth(1).minusDays(1);



    @Test
    @Order(10)
    @WithUserDetails("Test Expenses Manager User1")
    public void testDeleteSpendingRecord() throws Exception {
        Expense testExpense = expenseRepo.findAll().get(0);
        SpendingRecord testSpendingRecord = spendingRecordRepo.findAll().get(0);
        MockHttpServletRequestBuilder deleteSpendingRecord = post(
                "/delete-spending-record/" + testSpendingRecord.getId());
        mockMvc.perform(deleteSpendingRecord)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-expenditures-current-month"));
        expenseRepo.delete(testExpense);
    }

    @Test
    @Order(4)
    @WithUserDetails("Test Expenses Manager User1")
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
    @Order(6)
    @WithUserDetails("Test Expenses Manager User1")
    public void testSearchTasksByMonthAndYear()
            throws Exception {
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
                        containsString("NTD")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Expense 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("200.0")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Expenses Manager User1")))
                .andExpect(view().name(
                        "expenses/user-spending-records-by-month"));
    }

    @Test
    @Order(5)
    @WithUserDetails("Test Expenses Manager User1")
    public void testShowAllUserSpendingRecordsForCurrentMonth()
            throws Exception {
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
                        containsString("NTD")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Expense 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("200.0")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Expenses Manager User")))
                .andExpect(view().name(
                        "expenses/user-spending-records-by-month"));
    }

    @Test
    @Order(3)
    @WithUserDetails("Test Expenses Manager User1")
    public void testShowRecordExpenditureFormPage() throws Exception {
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
                        containsString("Taiwan New Dollar")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("United States Dollar")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Euro Member Countries")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Expense 1")))
                .andExpect(view().name(
                        "expenses/record-expenditure"));
    }


    @Test
    @Order(1)
    @WithUserDetails("Test Expenses Manager User1")
    public void testSubmitNewSpendingRecord()
            throws Exception {
        UserPrincipal testUser = userPrincipalRepo.findAll().get(0);
        Currency testCurrency = currencyRepo.findAll().get(0);
        Expense testExpense1 = expenseRepo.save(Expense.builder()
                .id(1L)
                .expenseName("Test Expense 1")
                .user(testUser)
                .build());
        MockHttpServletRequestBuilder createAccount = post(
                "/submit-spending-record")
                .param("expenseId", testExpense1.getId().toString())
                .param("date", monthBegin.toString())
                .param("amount", String.valueOf(200.00))
                .param("currencyId", testCurrency.getId().toString());;
        mockMvc.perform(createAccount)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-expenditures-current-month"));
    }
}
