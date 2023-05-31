package ExpensesTracker.ExpensesTracker.services.accounts;
import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.repositories.accounts.BankRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes= ExpensesTrackerApplication.class)
@ActiveProfiles("test")

public class BankServiceUnitTest {

    @MockBean
    BankRepo bankRepo;

    @Autowired
    BankService bankService;

    Bank testBank1 = Bank.builder()
            .id(1L)
            .bankName("Test Bank 1")
            .build();

    Bank testBank2 = Bank.builder()
            .id(2L)
            .bankName("Test Bank 2")
            .build();

    @Test
    public void testGetAllBanks() {
        List<Bank> allBanks = new ArrayList<>();
        allBanks.add(testBank1);
        allBanks.add(testBank2);
        when(bankRepo
                .findAllByOrderByBankName())
                .thenReturn(allBanks);
        assertThat(
                bankService.getAllBanks())
                .isEqualTo(allBanks);
        assertThat(
                bankService.getAllBanks())
                        .size()
                .isEqualTo(allBanks.size());
    }

    // it should return null because the id is
    // for a non-existent Bank object
    @Test
    public void testGetBankFailureBehavior() {
        when(bankRepo.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThat(bankService.getBank(1L))
                .isEqualTo(null);
    }

    @Test
    public void testGetBankSuccessBehavior() {
        when(bankRepo.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testBank1));
        assertThat(bankService.getBank(1L))
                .isEqualTo(testBank1);
    }

    @Test
    public void testSaveBankFailureBehavior()
            throws IllegalArgumentException {
        when(bankRepo.save(any(Bank.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            bankService.saveBank(testBank1);
        });
    }

    @Test
    public void testSaveBankSuccessBehavior()
            throws IllegalArgumentException {
        when(bankRepo.save(any(Bank.class)))
                .thenReturn(testBank1);
        assertThat(bankService.saveBank(testBank1))
                .isEqualTo(testBank1);
    }

}
