package ExpensesTracker.ExpensesTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

// this version of the command line runner exists to run unit tests of the
// UserDetailsServiceImp. This is because the UserRepo used in the
// ExpensesTrackerApplicationTest command line runner clashes with the Mocked UserRepo
// in the UserDetailsServiceImpUnitTest class

@SpringBootApplication
@Profile("test-user")
public class ExpensesTrackerApplicationUserTest {

    public static void main(String[] args) {
        SpringApplication
                .run(ExpensesTrackerApplication.class, args);
    }


}
