package ExpensesTracker.ExpensesTracker.services.users;
import ExpensesTracker.ExpensesTracker.models.user.Authority;
import ExpensesTracker.ExpensesTracker.models.user.UserMeta;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.models.user.forms.UserRegistrationForm;
import ExpensesTracker.ExpensesTracker.repositories.user.AuthorityRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.UserMetaRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.UserPrincipalRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    UserPrincipalRepo userPrincipalRepo;

    @Autowired
    AuthorityRepo authorityRepo;

    @Autowired
    UserMetaRepo userMetaRepo;

    public UserPrincipal createNewAdminUser(UserRegistrationForm userRegistration) {
        // the ids of the different authorities are
        // set in the bootstrapping class
        // these are the two authorities given to admins
        Authority userAuth = authorityRepo.getById(1L);
        Authority mainAuth = authorityRepo.getById(2L);
        UserMeta userMeta = UserMeta.builder()
                .surname(userRegistration.getSurname())
                .givenName(userRegistration.getGivenName())
                .email(userRegistration.getEmail())
                .age(userRegistration.getAge())
                .build();
        UserPrincipal newUser = new UserPrincipal(userRegistration.getUsername(),
                passwordEncoder.encode(userRegistration.getPassword()),
                Arrays.asList(userAuth, mainAuth), userMeta);
        return userPrincipalRepo.save(newUser);
    }

    public UserPrincipal createNewExpensesManagerUser(UserRegistrationForm userRegistration) {
        // the ids of the different authorities are
        // set in the bootstrapping class
        // these are the two authorities given to expenses managers
        Authority userAuth = authorityRepo.getById(1L);
        Authority mainAuth = authorityRepo.getById(3L);
        UserMeta userMeta = UserMeta.builder()
                .surname(userRegistration.getSurname())
                .givenName(userRegistration.getGivenName())
                .email(userRegistration.getEmail())
                .age(userRegistration.getAge())
                .build();
        UserPrincipal newUser = new UserPrincipal(userRegistration.getUsername(),
                passwordEncoder.encode(userRegistration.getPassword()),
                Arrays.asList(userAuth, mainAuth), userMeta);
        return userPrincipalRepo.save(newUser);
    }

    @Override
    public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        return userPrincipalRepo.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username or email : " + username)
        );
    }
}
