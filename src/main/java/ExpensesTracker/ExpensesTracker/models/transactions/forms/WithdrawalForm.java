package ExpensesTracker.ExpensesTracker.models.transactions.forms;

import lombok.*;

import java.math.BigDecimal;

// this is used as a form to get the IDS of the associated fields
// rather than the associated objects themselves to allow for easier
// testing by mocking the service method that queries the associated
// field object by ID
@Getter
@Builder
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class WithdrawalForm {

    private Long savingsAccountId;

    private String date;

    private BigDecimal amount;

}
