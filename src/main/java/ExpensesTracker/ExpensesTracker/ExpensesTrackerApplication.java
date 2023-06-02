package ExpensesTracker.ExpensesTracker;

import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.models.user.Authority;
import ExpensesTracker.ExpensesTracker.models.user.AuthorityEnum;
import ExpensesTracker.ExpensesTracker.repositories.currency.CurrencyRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.AuthorityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

import java.util.Arrays;

@SpringBootApplication
public class ExpensesTrackerApplication implements CommandLineRunner {

	@Autowired
	private AuthorityRepo authorityRepo;

	@Autowired
	private CurrencyRepo currencyRepo;

	public static void main(String[] args) {

		SpringApplication.run(ExpensesTrackerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (authorityRepo.findAll().isEmpty()) {
			// constructs the Authorities for the different user types and
			// saves to database. This is a key step before creating any users
			Authority userAuth = Authority.builder().id(1L)
					.authority(AuthorityEnum.ROLE_USER).build();
			Authority adminAuth = Authority.builder().id(2L)
					.authority(AuthorityEnum.ROLE_ADMIN).build();
			Authority mainAuth = Authority.builder().id(3L)
					.authority(AuthorityEnum.ROLE_EXPENSES_MANAGER).build();
			authorityRepo.saveAll(Arrays.asList(userAuth, adminAuth, mainAuth));
		}

		if (currencyRepo.findAll().isEmpty()) {
			Currency ntd = Currency.builder()
					.id(1L)
					.currencyCode("NTD")
					.currencyName("Taiwan New Dollar").build();
			Currency usd = Currency.builder()
					.id(2L)
					.currencyCode("USD")
					.currencyName("United States Dollar").build();
			Currency eur = Currency.builder()
					.id(3L)
					.currencyCode("EUR")
					.currencyName("Euro Member Countries").build();
			currencyRepo.saveAll(Arrays.asList(ntd, usd, eur));
		}
	}

}
