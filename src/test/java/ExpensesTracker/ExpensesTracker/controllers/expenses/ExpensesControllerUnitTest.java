package ExpensesTracker.ExpensesTracker.controllers.expenses;

import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.controllers.income.IncomeSourceController;
import ExpensesTracker.ExpensesTracker.models.expenses.Expense;
import ExpensesTracker.ExpensesTracker.models.income.IncomeSource;
import ExpensesTracker.ExpensesTracker.models.user.Authority;
import ExpensesTracker.ExpensesTracker.models.user.AuthorityEnum;
import ExpensesTracker.ExpensesTracker.models.user.UserMeta;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.repositories.currency.CurrencyRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.AuthorityRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.UserPrincipalRepo;
import ExpensesTracker.ExpensesTracker.services.accounts.BankService;
import ExpensesTracker.ExpensesTracker.services.expenses.ExpenseService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ExpensesController.class)
@ContextConfiguration(classes = {ExpensesTrackerApplication.class})
@ActiveProfiles("test")
public class ExpensesControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthorityRepo authorityRepo;

    @MockBean
    BankService bankService;

    @MockBean
    CurrencyRepo currencyRepo;

    @MockBean
    ExpenseService expenseService;

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

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testSaveNewExpense() throws Exception {
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(expenseService.saveExpense(any(Expense.class)))
                .thenReturn(testExpense1);
        MockHttpServletRequestBuilder createExpense =
                post("/submit-expense")
                        .with(csrf())
                        .param("expenseName", "Test Expense 1");
        mockMvc.perform(createExpense)
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-expenses"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowAllUsersExpenses() throws Exception {
        List<Expense> usersExpenses = new ArrayList<>();
        usersExpenses.add(testExpense1);
        usersExpenses.add(testExpense2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(expenseService.getAllExpensesByUserUsername(anyString()))
                .thenReturn(usersExpenses);
        mockMvc
                .perform(get("/user-expenses"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("expenses"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Expense 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Expense 2")))
                .andExpect(view().name(
                        "expenses/user-expenses"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowCreateExpensePage() throws Exception {
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
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
