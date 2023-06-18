package ExpensesTracker.ExpensesTracker.controllerEndpoints.expenses;

import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.models.expenses.Expense;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.repositories.expenses.ExpenseRepo;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(classes = ExpensesTrackerApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class ExpensesControllerEndpointTest {

    @Autowired
    ExpenseRepo expenseRepo;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserPrincipalRepo userPrincipalRepo;

    @Test
    @Order(10)
    @WithUserDetails("Test Expenses Manager User1")
    public void testDeleteExpense() throws Exception {
        Expense testExpense = expenseRepo.findAll().get(0);
        MockHttpServletRequestBuilder deleteExpense = post(
                "/delete-expense/" + testExpense.getId());
        mockMvc.perform(deleteExpense)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-expenses"));
    }

    @Test
    @Order(1)
    @WithUserDetails("Test Expenses Manager User1")
    public void testSaveNewExpense()
            throws Exception {
        UserPrincipal testUser = userPrincipalRepo.findAll().get(0);
        MockHttpServletRequestBuilder createAccount = post(
                "/submit-expense")
                .param("expenseName", "Test Expense 1");
        mockMvc.perform(createAccount)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-expenses"));
    }


    @Test
    @Order(2)
    @WithUserDetails("Test Expenses Manager User1")
    public void testShowAllUsersExpenses() throws Exception {
        mockMvc
                .perform(get("/user-expenses"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("expenses"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Expense 1")))
                .andExpect(view().name(
                        "expenses/user-expenses"));
    }

    @Test
    @Order(3)
    @WithUserDetails("Test Expenses Manager User1")
    public void testShowCreateExpensePage() throws Exception {
        mockMvc
                .perform(get("/create-expense"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("expense"))
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("expenses/create-expense"));
    }
}
