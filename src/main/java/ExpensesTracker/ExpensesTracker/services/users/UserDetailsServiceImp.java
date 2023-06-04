package ExpensesTracker.ExpensesTracker.services.users;
import ExpensesTracker.ExpensesTracker.logging.Loggable;
import ExpensesTracker.ExpensesTracker.models.user.Authority;
import ExpensesTracker.ExpensesTracker.models.user.UserMeta;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import ExpensesTracker.ExpensesTracker.models.user.forms.UserRegistrationForm;
import ExpensesTracker.ExpensesTracker.repositories.user.AuthorityRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.UserMetaRepo;
import ExpensesTracker.ExpensesTracker.repositories.user.UserPrincipalRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Objects;
import java.util.Set;

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


    // this is used during registration to confirm that the
    // two passwords submitted are the same
    @Loggable
    public boolean confirmPasswordsMatch(UserRegistrationForm userRegistration) {
        return Objects.equals(userRegistration.getPassword(),
                userRegistration.getPasswordConfirmation());
    }

    @Loggable
    public UserPrincipal createNewAdminUser(
            UserRegistrationForm userRegistration) {
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

    @Loggable
    public UserPrincipal createNewExpensesManagerUser(
            UserRegistrationForm userRegistration)
    {
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

    @Loggable
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteUserPrincipal(long id) {
        UserPrincipal user = userPrincipalRepo.findById(id).get();
        List<Authority> authorities = user.getAuthorities();
        while(authorities.iterator().hasNext())
        {
            Authority authority = authorities.iterator().next();
            Set<UserPrincipal> users = authority.getUsers();
            users.remove(user);
            authority.setUsers(users);
            authorityRepo.save(authority);
            authorities.remove(authority);
        }
        user.setAuthorities(authorities);
        userPrincipalRepo.save(user);
        userPrincipalRepo.deleteById(id);
    }

    // this is to clean up users after testing
    @Loggable
    public void deleteUserPrincipalByUsername(String username) {
        UserPrincipal user = userPrincipalRepo.findByUsername(username).get();
        List<Authority> authorities = user.getAuthorities();
        while(authorities.iterator().hasNext())
        {
            Authority authority = authorities.iterator().next();
            Set<UserPrincipal> users = authority.getUsers();
            users.remove(user);
            authority.setUsers(users);
            authorityRepo.save(authority);
            authorities.remove(authority);
        }
        user.setAuthorities(authorities);
        userPrincipalRepo.save(user);
        userPrincipalRepo.deleteByUsername(username);
    }

    @Loggable
    public List<UserPrincipal> getAllExpensesManagers() {
        return
                userPrincipalRepo.findByExpensesManagerAuthority();
    }

    @Loggable
    public UserPrincipal getUserById(Long id) {
        return userPrincipalRepo.findById(id)
                .orElse(null);
    }

    @Loggable
    @Override
    public UserPrincipal loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return userPrincipalRepo.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(
                        "User not found with username or email : "
                                + username)
        );
    }

    // this is to test whether or not a username is available during registration
    // to prevent database error due to unique constraint violation
    @Loggable
    public boolean usernameAlreadyExists(String username) {
        try {
            UserPrincipal existentUser = loadUserByUsername(username);
            return true;
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }
}
