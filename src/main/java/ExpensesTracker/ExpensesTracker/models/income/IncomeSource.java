package ExpensesTracker.ExpensesTracker.models.income;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncomeSource {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String incomeSourceName;

    @ManyToOne(optional = false)
    @JoinColumn
    private UserPrincipal user;

}
