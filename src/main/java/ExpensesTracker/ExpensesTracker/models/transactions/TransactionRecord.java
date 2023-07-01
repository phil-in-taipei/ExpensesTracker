package ExpensesTracker.ExpensesTracker.models.transactions;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class TransactionRecord {
    private Deposit depositRecord;

    private String transactionType;

    private Withdrawal withdrawalRecord;

    public TransactionRecord(Deposit deposit){
        this.depositRecord=deposit;
        this.transactionType="DEPOSIT";
    }

    public TransactionRecord(Withdrawal withdrawal){
        this.withdrawalRecord=withdrawal;
        this.transactionType="WITHDRAWAL";
    }
}
