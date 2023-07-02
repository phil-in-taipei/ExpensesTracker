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
        while (dateInMonth.isBefore(lastDate.plusDays(1))) {
            System.out.println("---------------------------------------------------------------------------");
            System.out.println("This is the date: " + dateInMonth);
            if (!depositsInDateRange.isEmpty()) {
                List<Deposit> depositsOnDate = new ArrayList<>();
                for (Deposit deposit : depositsInDateRange) {
                    if (dateInMonth.isEqual(deposit.getDate())){
                        System.out.println("***********A deposit was on that date, adding to list*********");
                        depositsOnDate.add(deposit);
                        TransactionRecord transactionRecordOnDate = new TransactionRecord(deposit);
                        transactionRecords.add(transactionRecordOnDate);
                    }
                    if (deposit.getDate().isAfter(dateInMonth)){
                        break;
                    }
                }
                depositsInDateRange.removeAll(depositsOnDate);
            }
            if(!withdrawalsInDateRange.isEmpty()) {
                List<Withdrawal> withdrawalsOnDate =  new ArrayList<>();
                for (Withdrawal withdrawal : withdrawalsInDateRange) {
                    if (dateInMonth.isEqual(withdrawal.getDate())) {
                        System.out.println("***********A withdrawal was on that date, adding to list*********");
                        withdrawalsOnDate.add(withdrawal);
                        TransactionRecord transactionRecordOnDate = new TransactionRecord(withdrawal);
                        transactionRecords.add(transactionRecordOnDate);
                    }
                    if (withdrawal.getDate().isAfter(dateInMonth)){
                        break;
                    }
                }
                withdrawalsInDateRange.removeAll(withdrawalsOnDate);
            }
            dateInMonth = dateInMonth.plusDays(1);
        }
        return transactionRecords;
    }

}
