package ExpensesTracker.ExpensesTracker.services.expenses;
import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.models.user.Authority;
import ExpensesTracker.ExpensesTracker.models.user.AuthorityEnum;
import ExpensesTracker.ExpensesTracker.models.user.UserMeta;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.models.expenses.Expense;
import ExpensesTracker.ExpensesTracker.repositories.expenses.ExpenseRepo;
import ExpensesTracker.ExpensesTracker.services.users.UserDetailsServiceImp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes= ExpensesTrackerApplication.class)
@ActiveProfiles("test")
public class ExpenseServiceUnitTest {

    @MockBean
    ExpenseRepo expenseRepo;

    @Autowired
    ExpenseService expenseService;

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
            .id(2L)
            .expenseName("Test Expense 2")
            .user(testUser)
            .build();

    @Test
    public void testGetAllExpensesByUserUsername() {
        List<Expense> usersExpenses = new ArrayList<>();
        usersExpenses.add(testExpense1);
        usersExpenses.add(testExpense2);
        when(expenseRepo.findAllByUserUsernameOrderByExpenseName(
                anyString()))
                .thenReturn(usersExpenses);
        assertThat(expenseService.getAllExpensesByUserUsername("testuser"))
                .isEqualTo(usersExpenses);
    }

    @Test
    public void testSaveExpenseFailure()
            throws IllegalArgumentException {
        when(expenseRepo.save(any(Expense.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            expenseService.saveExpense(testExpense1);
        });
    }

    @Test
    public void testSaveExpenseSuccess()
            throws IllegalArgumentException {
        when(expenseRepo.save(any(Expense.class)))
        .thenReturn(testExpense1);
        assertThat(expenseService.saveExpense(testExpense1))
                .isEqualTo(testExpense1);
    }
}
