package ExpensesTracker.ExpensesTracker.models.expenses;

import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpendingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Timestamp time;

    @ManyToOne(optional = false)
    private Expense expense;
}
