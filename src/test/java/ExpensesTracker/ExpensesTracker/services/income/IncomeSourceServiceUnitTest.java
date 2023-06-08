package ExpensesTracker.ExpensesTracker.services.income;
import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.models.income.IncomeSource;
import ExpensesTracker.ExpensesTracker.models.user.Authority;
import ExpensesTracker.ExpensesTracker.models.user.AuthorityEnum;
import ExpensesTracker.ExpensesTracker.models.user.UserMeta;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.repositories.income.IncomeSourceRepo;
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
public class IncomeSourceServiceUnitTest {

    @MockBean
    IncomeSourceRepo incomeSourceRepo;

    @Autowired
    IncomeSourceService incomeSourceService;

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

    IncomeSource testIncomeSource1 = IncomeSource.builder()
            .id(1L)
            .incomeSourceName("Test Income Source 1")
            .user(testUser)
            .build();

    IncomeSource testIncomeSource2 = IncomeSource.builder()
            .id(2L)
            .incomeSourceName("Test Income Source 2")
            .user(testUser)
            .build();

    @Test
    public void testGetAllIncomeSourcesByUserUsername() {
        List<IncomeSource> usersIncomeSources = new ArrayList<>();
        usersIncomeSources.add(testIncomeSource1);
        usersIncomeSources.add(testIncomeSource2);
        when(incomeSourceRepo.findAllByUserUsernameOrderByIncomeSourceName(anyString()))
                .thenReturn(usersIncomeSources);
        assertThat(incomeSourceService.getAllIncomeSourcesByUserUsername("testuser"))
                .isEqualTo(usersIncomeSources);
    }

    @Test
    public void testSaveIncomeSourceFailure() throws IllegalArgumentException {
        when(incomeSourceRepo.save(
                any(IncomeSource.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            incomeSourceService.saveIncomeSource(testIncomeSource1);
        });
    }

    @Test
    public void testSaveIncomeSourceSuccess()
            throws IllegalArgumentException {
        when(incomeSourceRepo.save(
                any(IncomeSource.class)))
                .thenReturn(testIncomeSource1);
        assertThat(incomeSourceService.saveIncomeSource(testIncomeSource1))
                .isEqualTo(testIncomeSource1);
    }
}
