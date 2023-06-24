package ExpensesTracker.ExpensesTracker.services.transactions;

import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.models.income.IncomeSource;
import ExpensesTracker.ExpensesTracker.models.transactions.Deposit;
import ExpensesTracker.ExpensesTracker.models.transactions.Withdrawal;
import ExpensesTracker.ExpensesTracker.models.user.Authority;
import ExpensesTracker.ExpensesTracker.models.user.AuthorityEnum;
import ExpensesTracker.ExpensesTracker.models.user.UserMeta;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.repositories.transactions.DepositRepo;
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
public class DepositServiceUnitTest {

    @MockBean
    DepositRepo depositRepo;

    @Autowired
    DepositService depositService;


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

    IncomeSource testIncomeSource1 = IncomeSource.builder()
            .id(1L)
            .incomeSourceName("Test Income Source 1")
            .user(testUser)
            .build();

    LocalDate today = LocalDate.now();
    LocalDate firstDayOfThisMonth = today.withDayOfMonth(1);
    LocalDate lastDayOfThisMonth = firstDayOfThisMonth.plusMonths(1)
            .minusDays(1);

    Deposit testDeposit1 = Deposit.builder()
            .id(1L)
            .date(firstDayOfThisMonth.plusDays(1))
            .depositAmount(BigDecimal.valueOf(100.00))
            .incomeSource(testIncomeSource1)
            .savingsAccount(testAccount1)
            .build();

    Deposit testDeposit2 = Deposit.builder()
            .id(2L)
            .date(lastDayOfThisMonth.minusDays(1))
            .depositAmount(BigDecimal.valueOf(100.00))
            .incomeSource(testIncomeSource1)
            .savingsAccount(testAccount1)
            .build();

    @Test
    public void testGetAllDepositsBySavingsAccountInDateRange() {
        List<Deposit> userDeposits = new ArrayList<>();
        userDeposits.add(testDeposit1);
        userDeposits.add(testDeposit2);
        when(depositRepo
                .findAllBySavingsAccountIdAndDateBetweenOrderByDateDesc(
                        anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(userDeposits);
        assertThat(
                depositService.getAllDepositsBySavingsAccountInDateRange(
                        testAccount1.getId(), firstDayOfThisMonth, lastDayOfThisMonth))
                .isEqualTo(userDeposits);
        assertThat(
                depositService.getAllDepositsBySavingsAccountInDateRange(
                        testAccount1.getId(), firstDayOfThisMonth, lastDayOfThisMonth).size())
                .isEqualTo(userDeposits.size());
    }

    @Test
    public void testGetAllUserDepositsInDateRange() {
        List<Deposit> userDeposits = new ArrayList<>();
        userDeposits.add(testDeposit1);
        userDeposits.add(testDeposit2);
        when(depositRepo
                .findAllBySavingsAccount_UserUsernameAndDateBetweenOrderByDateDesc(
                        anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(userDeposits);
        assertThat(
                depositService.getAllUserDepositsInDateRange(
                        "testuser", firstDayOfThisMonth, lastDayOfThisMonth))
                .isEqualTo(userDeposits);
        assertThat(
                depositService.getAllUserDepositsInDateRange(
                        "testuser", firstDayOfThisMonth, lastDayOfThisMonth).size())
                .isEqualTo(userDeposits.size());

    }

    // it should return null because the id is for a
    // non-existent Deposit object
    @Test
    public void testGetDepositFailureBehavior() {
        when(depositRepo.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThat(depositService.getDeposit(1L))
                .isEqualTo(null);
    }

    @Test
    public void testGetDepositSuccessBehavior() {
        when(depositRepo.findById(anyLong()))
                .thenReturn(Optional.of(testDeposit1));
        assertThat(depositService.getDeposit(1L))
                .isEqualTo(testDeposit1);
    }

    @Test
    public void testSaveDepositFailureBehavior()
            throws IllegalArgumentException {
        when(depositRepo.save(any(Deposit.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            depositService.saveDeposit(testDeposit1);
        });
    }

    @Test
    public void testSaveDepositSuccessBehavior()
            throws IllegalArgumentException {
        when(depositRepo.save(any(Deposit.class)))
                .thenReturn(testDeposit1);
        assertThat(depositService.saveDeposit(testDeposit1))
                .isEqualTo(testDeposit1);
    }
}
