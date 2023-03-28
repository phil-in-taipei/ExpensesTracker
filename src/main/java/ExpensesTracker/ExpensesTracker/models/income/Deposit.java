package ExpensesTracker.ExpensesTracker.models.income;

import ExpensesTracker.ExpensesTracker.models.accounts.SavingsAccount;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private LocalDateTime time;

}
