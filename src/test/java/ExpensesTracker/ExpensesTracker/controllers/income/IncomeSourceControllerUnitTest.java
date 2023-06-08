package ExpensesTracker.ExpensesTracker.controllers.income;

import ExpensesTracker.ExpensesTracker.ExpensesTrackerApplication;
import ExpensesTracker.ExpensesTracker.controllers.accounts.SavingsAccountsController;
import ExpensesTracker.ExpensesTracker.models.income.IncomeSource;
import ExpensesTracker.ExpensesTracker.models.user.Authority;
import ExpensesTracker.ExpensesTracker.models.user.AuthorityEnum;
import ExpensesTracker.ExpensesTracker.models.user.UserMeta;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.repositories.currency.CurrencyRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.AuthorityRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.UserPrincipalRepo;
import ExpensesTracker.ExpensesTracker.services.accounts.BankService;
import ExpensesTracker.ExpensesTracker.services.income.IncomeSourceService;
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

@WebMvcTest(IncomeSourceController.class)
//@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {ExpensesTrackerApplication.class})
@ActiveProfiles("test")
public class IncomeSourceControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthorityRepo authorityRepo;

    @MockBean
    BankService bankService;

    @MockBean
    CurrencyRepo currencyRepo;

    @MockBean
    IncomeSourceService incomeSourceService;

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
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testSaveNewIncomeSource() throws Exception {
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(incomeSourceService.saveIncomeSource(any(IncomeSource.class)))
                .thenReturn(testIncomeSource1);
        MockHttpServletRequestBuilder createIncomeSource =
                post("/submit-income-source")
                        .with(csrf())
                        .param("incomeSourceName", "Test Income Source 1");
        mockMvc.perform(createIncomeSource)
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-income-sources"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowAllUsersIncomeSources() throws Exception {
        List<IncomeSource> usersIncomeSources = new ArrayList<>();
        usersIncomeSources.add(testIncomeSource1);
        usersIncomeSources.add(testIncomeSource2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(incomeSourceService.getAllIncomeSourcesByUserUsername(anyString()))
                .thenReturn(usersIncomeSources);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        mockMvc
                .perform(get("/user-income-sources"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("incomeSources"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Income Source 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Income Source 2")))
                .andExpect(view().name(
                        "income/user-income-sources"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowSubmitIncomeSourcePage() throws Exception {
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        mockMvc
                .perform(get("/create-income-source"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("incomeSource"))
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("income/create-income-source"));
    }
}
