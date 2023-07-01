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
                Deposit nextDepositInList =  depositsInDateRange.get(0);
                System.out.println("This is the next deposit in the list");
                System.out.println(nextDepositInList.getDate());
                if (dateInMonth.isEqual(nextDepositInList.getDate())){
                    System.out.println("***********A deposit was on that date, adding to list*********");
                    TransactionRecord transactionRecordOnDate = new TransactionRecord(nextDepositInList);
                    transactionRecords.add(transactionRecordOnDate);
                    depositsInDateRange.remove(nextDepositInList);
                }
            }
            if(!withdrawalsInDateRange.isEmpty()) {
                Withdrawal nextWithdrawalInList =  withdrawalsInDateRange.get(0);
                System.out.println("This is the next withdrawal in the list: ");
                System.out.println(nextWithdrawalInList.getDate());
                if (dateInMonth.isEqual(nextWithdrawalInList.getDate())) {
                    System.out.println("***********A withdrawal was on that date, adding to list*********");
                    TransactionRecord transactionRecordOnDate = new TransactionRecord(nextWithdrawalInList);
                    transactionRecords.add(transactionRecordOnDate);
                    withdrawalsInDateRange.remove(nextWithdrawalInList);
                }
            }
            dateInMonth = dateInMonth.plusDays(1);
        }
        return transactionRecords;
    }

}
