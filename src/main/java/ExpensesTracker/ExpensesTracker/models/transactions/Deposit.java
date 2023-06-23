package ExpensesTracker.ExpensesTracker.models.transactions;

import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.models.income.IncomeSource;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Deposit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private  BigDecimal depositAmount;

    @ManyToOne(optional = false)
    @JoinColumn
    private IncomeSource incomeSource;

    @ManyToOne(optional = false)
    @JoinColumn
    private SavingsAccount savingsAccount;

    @Column(nullable = false)
    private LocalDate date;

}
