package ExpensesTracker.ExpensesTracker.services.transactions;
import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.models.transactions.Deposit;
import ExpensesTracker.ExpensesTracker.models.transactions.Withdrawal;
import ExpensesTracker.ExpensesTracker.models.user.Authority;
import ExpensesTracker.ExpensesTracker.models.user.AuthorityEnum;
import ExpensesTracker.ExpensesTracker.models.user.UserMeta;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.repositories.transactions.WithdrawalRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@SpringBootTest(classes= ExpensesTrackerApplication.class)
@ActiveProfiles("test")
public class WithdrawalServiceUnitTest {

    @MockBean
    WithdrawalRepo withdrawalRepo;

    @Autowired
    WithdrawalService withdrawalService;

    Bank testBank = Bank.builder()
            .id(1L)
            .bankName("Test Bank 1")
            .build();

    Currency testCurrency = Currency.builder()
            .id(1L)
            .currencyCode("TCC")
            .currencyName("Test Currency").build();

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

    SavingsAccount testAccount1 = SavingsAccount.builder()
            .id(1L)
            .accountBalance(BigDecimal.valueOf(0.00))
            .accountName("Test Bank Account 1")
            .bank(testBank)
            .currency(testCurrency)
            .user(testUser)
            .build();

    LocalDate today = LocalDate.now();
    LocalDate firstDayOfThisMonth = today.withDayOfMonth(1);
    LocalDate lastDayOfThisMonth = firstDayOfThisMonth.plusMonths(1)
            .minusDays(1);

    Withdrawal testWithdrawal1 = Withdrawal.builder()
            .id(1L)
            .amount(BigDecimal.valueOf(100.00))
            .savingsAccount(testAccount1)
            .date(firstDayOfThisMonth.plusDays(1))
            .build();

    Withdrawal testWithdrawal2 = Withdrawal.builder()
            .id(2L)
            .amount(BigDecimal.valueOf(100.00))
            .savingsAccount(testAccount1)
            .date(lastDayOfThisMonth.minusDays(1))
            .build();

    @Test
    public void testGetAllUserWithdrawalsInDateRange() {
        List<Withdrawal> userWithdrawals = new ArrayList<>();
        userWithdrawals.add(testWithdrawal1);
        userWithdrawals.add(testWithdrawal2);
        when(withdrawalRepo
                .findAllBySavingsAccount_UserUsernameAndDateBetweenOrderByDateDesc(
                        anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(userWithdrawals);
        assertThat(
                withdrawalService.getAllUserWithdrawalsInDateRange(
                        "testuser", firstDayOfThisMonth, lastDayOfThisMonth))
                .isEqualTo(userWithdrawals);
        assertThat(
                withdrawalService.getAllUserWithdrawalsInDateRange(
                        "testuser", firstDayOfThisMonth, lastDayOfThisMonth).size())
                .isEqualTo(userWithdrawals.size());
    }

    @Test
    public void testGetAllWithdrawalsBySavingsAccountInDateRange() {
        List<Withdrawal> userWithdrawals = new ArrayList<>();
        userWithdrawals.add(testWithdrawal1);
        userWithdrawals.add(testWithdrawal2);
        when(withdrawalRepo
                .findAllBySavingsAccountIdAndDateBetweenOrderByDateAsc(
                        anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(userWithdrawals);
        assertThat(
                withdrawalService.getAllWithdrawalsBySavingsAccountInDateRange(
                        testAccount1.getId(), firstDayOfThisMonth, lastDayOfThisMonth))
                .isEqualTo(userWithdrawals);
        assertThat(
                withdrawalService.getAllWithdrawalsBySavingsAccountInDateRange(
                        testAccount1.getId(), firstDayOfThisMonth, lastDayOfThisMonth).size())
                .isEqualTo(userWithdrawals.size());
    }

    // it should return null because the id is for a
    // non-existent Withdrawal object
    @Test
    public void testGetWithdrawalFailureBehavior() {
        when(withdrawalRepo.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThat(withdrawalService.getWithdrawal(1L))
                .isEqualTo(null);
    }

    @Test
    public void testGetWithdrawalSuccessBehavior() {
        when(withdrawalRepo.findById(anyLong()))
                .thenReturn(Optional.of(testWithdrawal1));
        assertThat(withdrawalService.getWithdrawal(1L))
                .isEqualTo(testWithdrawal1);
    }

    @Test
    public void testSaveWithdrawalFailureBehavior()
            throws IllegalArgumentException {
        when(withdrawalRepo.save(any(Withdrawal.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            withdrawalService.saveWithdrawal(testWithdrawal1);
        });
    }

    @Test
    public void testSaveWithdrawalSuccessBehavior()
            throws IllegalArgumentException {
        when(withdrawalRepo.save(any(Withdrawal.class)))
                .thenReturn(testWithdrawal1);
        assertThat(withdrawalService.saveWithdrawal(testWithdrawal1))
                .isEqualTo(testWithdrawal1);
    }

}
