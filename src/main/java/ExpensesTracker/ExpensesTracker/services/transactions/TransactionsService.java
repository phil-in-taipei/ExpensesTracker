package ExpensesTracker.ExpensesTracker.services.transactions;

import ExpensesTracker.ExpensesTracker.logging.Loggable;
import ExpensesTracker.ExpensesTracker.models.transactions.Deposit;
import ExpensesTracker.ExpensesTracker.models.transactions.TransactionRecord;
import ExpensesTracker.ExpensesTracker.models.transactions.Withdrawal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionsService {

    @Autowired
    DepositService depositService;

    @Autowired
    WithdrawalService withdrawalService;

    // this will query both deposits and withdrawals during a date range
    // and concatenate them together into a ListArray of a TransactionRecord
    // datatype which can accept either datatype and a string specifying
    // which one
    @Loggable
    public List<TransactionRecord> getAllTransactionsBySavingsAccountInDateRange(
            Long accountId, LocalDate firstDate, LocalDate lastDate) {
        List<TransactionRecord> transactionRecords = new ArrayList<>();
        List<Deposit> depositsInDateRange = depositService
                .getAllDepositsBySavingsAccountInDateRange(
                        accountId, firstDate, lastDate);
        List<Withdrawal> withdrawalsInDateRange = withdrawalService
                .getAllWithdrawalsBySavingsAccountInDateRange(
                        accountId, firstDate, lastDate);
        LocalDate dateInMonth = firstDate;
        // iterating through all dates in the date range
        while (dateInMonth.isBefore(lastDate.plusDays(1))) {
            System.out.println("This is the date in the month: " + dateInMonth);
            // checks if any deposits were in the query during that date range
            if(!depositsInDateRange.isEmpty()) {
                List<Deposit> depositsOnDate = new ArrayList<>();
                for (Deposit deposit : depositsInDateRange) {
                    if (dateInMonth.isEqual(deposit.getDate())){
                        // if a date matches the deposit is added to a temporary ListArray
                        // and it is also inserted into a new TransactionRecord object,
                        // which is added to the monthly list array of TransactionRecords
                        depositsOnDate.add(deposit);
                        TransactionRecord transactionRecordOnDate = new TransactionRecord(deposit);
                        transactionRecords.add(transactionRecordOnDate);
                    }
                    if (deposit.getDate().isAfter(dateInMonth)){
                        // if the date is after the date in the date iteration
                        // it stops iterating through the list of deposits
                        break;
                    }
                }
                // removes the deposits already inserted into the TransactionsRecord list
                // to prevent unnecessary iterations
                depositsInDateRange.removeAll(depositsOnDate);
            }
        // checks if any withdrawals were in the query during that date range
        if(!withdrawalsInDateRange.isEmpty()) {
                List<Withdrawal> withdrawalsOnDate =  new ArrayList<>();
                for (Withdrawal withdrawal : withdrawalsInDateRange) {
                    // if a date matches the withdrawal is added to a temporary ListArray
                    // and it is also inserted into a new TransactionRecord object,
                    // which is added to the monthly list array of TransactionRecords
                    if (dateInMonth.isEqual(withdrawal.getDate())) {
                        withdrawalsOnDate.add(withdrawal);
                        TransactionRecord transactionRecordOnDate = new TransactionRecord(withdrawal);
                        transactionRecords.add(transactionRecordOnDate);
                    }
                    if (withdrawal.getDate().isAfter(dateInMonth)){
                        // if the date is after the date in the date iteration
                        // it stops iterating through the list of deposits
                        break;
                    }
                }
            // removes the withdrawals already inserted into the TransactionsRecord list
            // to prevent unnecessary iterations
                withdrawalsInDateRange.removeAll(withdrawalsOnDate);
            }
            // continue on to the next date in the date range
            dateInMonth = dateInMonth.plusDays(1);
        }
        return transactionRecords;
    }

}
