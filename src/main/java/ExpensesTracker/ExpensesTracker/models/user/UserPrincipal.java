package ExpensesTracker.ExpensesTracker.models.user;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPrincipal implements UserDetails  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    // set to ALL for removal of related UserMeta on deletion
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private UserMeta userMeta;

    // note: had to add cascade
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinTable(name = "user_authority_join_table",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    private List<Authority> authorities;

    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    public UserPrincipal(String username, String password, List<Authority> authorities, UserMeta userMeta) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.userMeta = userMeta;
        accountNonExpired = true;
        accountNonLocked = true;
        credentialsNonExpired = true;
        enabled = true;
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
                "id=" + id +
                ", username='" + username + '\'' +
                //", password='" + password + '\'' +
                ", userMeta=" + userMeta.toString() +
                ", authorities=" + authorities +
                //", accountNonExpired=" + accountNonExpired +
                //", accountNonLocked=" + accountNonLocked +
               // ", credentialsNonExpired=" + credentialsNonExpired +
                //", enabled=" + enabled +
                '}';
    }

    // this is for forms in thymeleaf to have a readable String
    // will be accessed by user with Admin privileges in panel
    public String getTemplateSelector() {
        return username + " ("  + this.getUserMeta().getGivenName() +  " "
                + this.getUserMeta().getSurname() + ")";
    }

}
