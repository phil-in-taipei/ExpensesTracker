package ExpensesTracker.ExpensesTracker.services.transactions;
import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.models.income.IncomeSource;
import ExpensesTracker.ExpensesTracker.models.transactions.Deposit;
import ExpensesTracker.ExpensesTracker.models.transactions.TransactionRecord;
import ExpensesTracker.ExpensesTracker.models.transactions.Withdrawal;
import ExpensesTracker.ExpensesTracker.models.user.Authority;
import ExpensesTracker.ExpensesTracker.models.user.AuthorityEnum;
import ExpensesTracker.ExpensesTracker.models.user.UserMeta;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.repositories.transactions.DepositRepo;
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

// two separate methods are called because the dependent mocked services --
// DepositService and WithdrawalService could only be mocked once per method

@SpringBootTest(classes= ExpensesTrackerApplication.class)
@ActiveProfiles("test")
public class TransactionsServiceUnitTest {

    @MockBean
    DepositService depositService;

    @Autowired
    TransactionsService transactionsService;

    @MockBean
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
            .depositAmount(BigDecimal.valueOf(200.00))
            .incomeSource(testIncomeSource1)
            .savingsAccount(testAccount1)
            .build();

    Withdrawal testWithdrawal1 = Withdrawal.builder()
            .id(1L)
            .amount(BigDecimal.valueOf(100.00))
            .savingsAccount(testAccount1)
            .date(firstDayOfThisMonth.plusDays(1))
            .build();

    Withdrawal testWithdrawal2 = Withdrawal.builder()
            .id(2L)
            .amount(BigDecimal.valueOf(200.00))
            .savingsAccount(testAccount1)
            .date(lastDayOfThisMonth.minusDays(1))
            .build();

    @Test
    public void testGetAllTransactionsBySavingsAccountInDateRangeOutputToString() {
        List<TransactionRecord> userTransactionRecords = new ArrayList<>();
        List<Deposit> userDeposits = new ArrayList<>();
        userDeposits.add(testDeposit1);
        userDeposits.add(testDeposit2);

        List<Withdrawal> userWithdrawals = new ArrayList<>();
        userWithdrawals.add(testWithdrawal1);
        userWithdrawals.add(testWithdrawal2);
        // adding deposits and withdrawals into the userTransactionsRecords List
        // in order of the dates of the different transactions
        // the constructor is overloaded, so either data type can be added in
        userTransactionRecords.add(new TransactionRecord(testDeposit1));
        userTransactionRecords.add(new TransactionRecord(testWithdrawal1));
        userTransactionRecords.add(new TransactionRecord(testDeposit2));
        userTransactionRecords.add(new TransactionRecord(testWithdrawal2));
        when(depositService
                .getAllDepositsBySavingsAccountInDateRange(
                        anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(userDeposits);
        when(withdrawalService
                .getAllWithdrawalsBySavingsAccountInDateRange(
                        anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(userWithdrawals);
        assertThat(
                transactionsService.getAllTransactionsBySavingsAccountInDateRange(
                        testAccount1.getId(), firstDayOfThisMonth, lastDayOfThisMonth).toString())
                .isEqualTo(userTransactionRecords.toString());
    }

    @Test
    public void testGetAllTransactionsBySavingsAccountInDateRangeListSize() {
        List<TransactionRecord> userTransactionRecords = new ArrayList<>();
        List<Deposit> userDeposits = new ArrayList<>();
        userDeposits.add(testDeposit1);
        userDeposits.add(testDeposit2);

        List<Withdrawal> userWithdrawals = new ArrayList<>();
        userWithdrawals.add(testWithdrawal1);
        userWithdrawals.add(testWithdrawal2);
        // adding deposits and withdrawals into the userTransactionsRecords List
        // in order of the dates of the different transactions
        // the constructor is overloaded, so either data type can be added in
        userTransactionRecords.add(new TransactionRecord(testDeposit1));
        userTransactionRecords.add(new TransactionRecord(testWithdrawal1));
        userTransactionRecords.add(new TransactionRecord(testDeposit2));
        userTransactionRecords.add(new TransactionRecord(testWithdrawal2));
        when(depositService
                .getAllDepositsBySavingsAccountInDateRange(
                        anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(userDeposits);
        when(withdrawalService
                .getAllWithdrawalsBySavingsAccountInDateRange(
                        anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(userWithdrawals);
        assertThat(
                transactionsService.getAllTransactionsBySavingsAccountInDateRange(
                        testAccount1.getId(), firstDayOfThisMonth, lastDayOfThisMonth).size())
                .isEqualTo(userTransactionRecords.size());
    }
}
