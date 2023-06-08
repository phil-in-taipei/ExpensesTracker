package ExpensesTracker.ExpensesTracker.models.expenses;
import ExpensesTracker.ExpensesTracker.models.user.UserPrincipal;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String expenseName;

    @ManyToOne(optional = false)
    @JoinColumn
    private UserPrincipal user;
}
