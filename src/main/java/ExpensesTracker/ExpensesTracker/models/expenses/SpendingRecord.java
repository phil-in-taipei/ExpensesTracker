package ExpensesTracker.ExpensesTracker.models.expenses;

import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

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

    @ManyToOne(optional = false)
    private Currency currency;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(optional = false)
    private Expense expense;
}
