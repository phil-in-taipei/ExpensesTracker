package ExpensesTracker.ExpensesTracker.controllerEndpoints.income;

import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.models.expenses.Expense;
import ExpensesTracker.ExpensesTracker.models.income.IncomeSource;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.repositories.income.IncomeSourceRepo;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ExpensesTrackerApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class IncomeEndpointTest {

    @Autowired
    IncomeSourceRepo incomeSourceRepo;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserPrincipalRepo userPrincipalRepo;

    @Test
    @Order(10)
    @WithUserDetails("Test Expenses Manager User1")
    public void testDeleteExpense() throws Exception {
        IncomeSource testIncomeSource = incomeSourceRepo.findAll().get(0);
        MockHttpServletRequestBuilder deleteIncomeSource = post(
                "/delete-income-source/" + testIncomeSource.getId());
        mockMvc.perform(deleteIncomeSource)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-income-sources"));
    }



        @Test
    @Order(1)
    @WithUserDetails("Test Expenses Manager User1")
    public void testSaveNewIncomeSource()
            throws Exception {
        UserPrincipal testUser = userPrincipalRepo.findAll().get(0);
        MockHttpServletRequestBuilder createAccount = post(
                "/submit-income-source")
                .param("incomeSourceName", "Test Income Source 1");
        mockMvc.perform(createAccount)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-income-sources"));
    }
}
