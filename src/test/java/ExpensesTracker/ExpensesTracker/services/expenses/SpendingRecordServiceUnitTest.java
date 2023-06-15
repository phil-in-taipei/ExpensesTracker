package ExpensesTracker.ExpensesTracker.services.expenses;
import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.models.expenses.Expense;
import ExpensesTracker.ExpensesTracker.models.expenses.SpendingRecord;
import ExpensesTracker.ExpensesTracker.models.user.Authority;
import ExpensesTracker.ExpensesTracker.models.user.AuthorityEnum;
import ExpensesTracker.ExpensesTracker.models.user.UserMeta;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.repositories.expenses.SpendingRecordRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes= ExpensesTrackerApplication.class)
@ActiveProfiles("test")
public class SpendingRecordServiceUnitTest {

    @MockBean
    SpendingRecordRepo spendingRecordRepo;

    @Autowired
    SpendingRecordService spendingRecordService;


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

    LocalDate today = LocalDate.now();
    LocalDate firstDayOfThisMonth = today.withDayOfMonth(1);
    LocalDate lastDayOfThisMonth = firstDayOfThisMonth.plusMonths(1)
            .minusDays(1);
    SpendingRecord testSpendingRecord1 = SpendingRecord.builder()
            .id(1L)
            .amount(BigDecimal.valueOf(100.00))
            .currency(testCurrency)
            .date(firstDayOfThisMonth)
            .expense(testExpense1)
            .build();

    SpendingRecord testSpendingRecord2 = SpendingRecord.builder()
            .id(2L)
            .amount(BigDecimal.valueOf(100.00))
            .currency(testCurrency)
            .date(lastDayOfThisMonth)
            .expense(testExpense1)
            .build();

    @Test
    public void testGetAllUserSpendingRecordsInDateRange() {
        List<SpendingRecord> spendingRecordsInCurrentMonth = new ArrayList<>();
        spendingRecordsInCurrentMonth.add(testSpendingRecord1);
        spendingRecordsInCurrentMonth.add(testSpendingRecord2);
        when(spendingRecordRepo.findAllByExpense_UserUsernameAndDateBetweenOrderByDateAsc(
                        anyString(),  eq(firstDayOfThisMonth), eq(lastDayOfThisMonth)
                )
        ).thenReturn(spendingRecordsInCurrentMonth);
        assertThat(
                spendingRecordService
                        .getAllUserSpendingRecordsInDateRange("testuser", firstDayOfThisMonth,
                                lastDayOfThisMonth))
                .isEqualTo(spendingRecordsInCurrentMonth);
        assertThat(
                spendingRecordService
                        .getAllUserSpendingRecordsInDateRange("testuser", firstDayOfThisMonth,
                                lastDayOfThisMonth).size())
                .isEqualTo(spendingRecordsInCurrentMonth.size());
    }

    @Test
    public void testSaveSpendingRecordFailure()
            throws IllegalArgumentException {
        when(spendingRecordRepo.save(any(SpendingRecord.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            spendingRecordService.saveSpendingRecord(testSpendingRecord1);
        });
    }

    @Test
    public void testSaveSpendingRecordSuccess()
            throws IllegalArgumentException {
        when(spendingRecordRepo.save(any(SpendingRecord.class)))
                .thenReturn(testSpendingRecord1);
        assertThat(spendingRecordService.saveSpendingRecord(testSpendingRecord1))
                .isEqualTo(testSpendingRecord1);
    }
}
