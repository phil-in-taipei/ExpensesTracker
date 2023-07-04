package ExpensesTracker.ExpensesTracker.models.transactions;

import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Withdrawal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    private SavingsAccount savingsAccount;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    @Override
    public String toString() {
        return "Withdrawal{" +
                "id=" + id +
                ", savingsAccount=" + savingsAccount +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}
