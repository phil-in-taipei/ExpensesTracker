package ExpensesTracker.ExpensesTracker.models.transactions;

import lombok.*;

// this is hybrid data type which can be associated with either a deposit or a withdrawal
// with a String field specifying the transaction type and the deposit or withdrawal associated
// via  an overloaded constructor
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

    @Override
    public String toString() {
        return "TransactionRecord{" +
                "depositRecord=" + depositRecord +
                ", transactionType='" + transactionType + '\'' +
                ", withdrawalRecord=" + withdrawalRecord +
                '}';
    }
}
