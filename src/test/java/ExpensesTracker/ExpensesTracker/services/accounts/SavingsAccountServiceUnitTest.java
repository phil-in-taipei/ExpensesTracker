package ExpensesTracker.ExpensesTracker.services.accounts;

import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.models.user.Authority;
import ExpensesTracker.ExpensesTracker.models.user.AuthorityEnum;
import ExpensesTracker.ExpensesTracker.models.user.UserMeta;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.models.user.forms.UserRegistrationForm;
import ExpensesTracker.ExpensesTracker.services.users.UserDetailsServiceImp;
import ExpensesTracker.ExpensesTracker.services.accounts.SavingsAccountService;
import ExpensesTracker.ExpensesTracker.repositories.accounts.SavingAccountsRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

@SpringBootTest(classes=ExpensesTrackerApplication.class)
@ActiveProfiles("test")
public class SavingsAccountServiceUnitTest {

    @MockBean
    SavingAccountsRepo savingsAccountsRepo;

    @Autowired
    SavingsAccountService savingsAccountService;

    @MockBean
    UserDetailsServiceImp userService;

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

    SavingsAccount testAccount2 = SavingsAccount.builder()
            .id(2L)
            .accountBalance(BigDecimal.valueOf(0.00))
            .accountName("Test Bank Account 2")
            .bank(testBank)
            .currency(testCurrency)
            .user(testUser)
            .build();


    @AfterEach
    void clearMockRepo() {
        savingsAccountsRepo.deleteAll();
    }

    @Test
    public void testGetAllAccountsByUserUsername() {
        List<SavingsAccount> userSavingsAccounts = new ArrayList<>();
        userSavingsAccounts.add(testAccount1);
        userSavingsAccounts.add(testAccount2);
        when(savingsAccountsRepo
                        .findAllByUserUsernameOrderByBank_BankNameAsc(
                                eq(testUser.getUsername())))
                .thenReturn(userSavingsAccounts);
        assertThat(
                savingsAccountService.getAllAccountsByUserUsername(
                        "testuser"))
                .isEqualTo(userSavingsAccounts);
        assertThat(
                savingsAccountService.getAllAccountsByUserUsername("testuser")
                        .size())
                .isEqualTo(userSavingsAccounts.size());
    }


    // it should return null because the id is for a non-existent
    // Savings Account object
    @Test
    public void testGetSavingsAccountFailureBehavior() {
        when(savingsAccountsRepo.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThat(savingsAccountService.getSavingsAccount(1L))
                .isEqualTo(null);
    }

    @Test
    public void testGetSavingsAccountSuccessBehavior() {
        when(savingsAccountsRepo.findById(anyLong()))
                .thenReturn(Optional.of(testAccount1));
        assertThat(savingsAccountService.getSavingsAccount(1L))
                .isEqualTo(testAccount1);
    }

    @Test
    public void testSaveSavingsAccountFailureBehavior()
            throws IllegalArgumentException {
        when(savingsAccountsRepo.save(any(SavingsAccount.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            savingsAccountService.saveSavingsAccount(testAccount1);
        });
    }

    @Test
    public void testSaveSavingsAccountSuccessBehavior()
            throws IllegalArgumentException {
        when(savingsAccountsRepo.save(any(SavingsAccount.class)))
                .thenReturn(testAccount1);
        assertThat(savingsAccountService.saveSavingsAccount(testAccount1))
                .isEqualTo(testAccount1);
    }

}
