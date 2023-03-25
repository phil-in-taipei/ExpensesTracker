package ExpensesTracker.ExpensesTracker.models.accounts;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavingsAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    BigDecimal accountBalance;

    // later possibly foreign key relation to currency
    // (with link to external api for conversion)
    @ManyToOne(optional = false)
    private Bank bank;

    @Column(nullable = false)
    private String accountName;

    @ManyToOne(optional = false)
    @JoinColumn
    private UserPrincipal user;

}
