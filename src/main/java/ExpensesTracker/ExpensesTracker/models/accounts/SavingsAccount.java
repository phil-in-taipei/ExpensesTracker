package ExpensesTracker.ExpensesTracker.models.accounts;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
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

    @Column(nullable = false)
    private String accountName;

    @ManyToOne(optional = false)
    private Bank bank;

    @ManyToOne(optional = false)
    @JoinColumn
    private Currency currency;


    @ManyToOne(optional = false)
    @JoinColumn
    private UserPrincipal user;

}
