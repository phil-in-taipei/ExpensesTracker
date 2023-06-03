package ExpensesTracker.ExpensesTracker.models.user.forms;
import lombok.*;

// this class is a mixture of fields from UserMeta and UserPrincipal
// it will be used to submit the registration form and then the data in the fields
// will be extracted to build a UserPrincipal class and a corresponding UserMeta relation class
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationForm {

    private String surname;

    private String givenName;

    private String email;

    private Integer age;

    private String username;

    private String password;

    private String passwordConfirmation;

    @Override
    public String toString() {
        return "UserRegistration{" +
                "surname='" + surname + '\'' +
                ", givenName='" + givenName + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", passwordConfirmation='" + passwordConfirmation + '\'' +
                '}';
    }

}
