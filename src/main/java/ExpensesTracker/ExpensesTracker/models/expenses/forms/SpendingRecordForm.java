package ExpensesTracker.ExpensesTracker.models.expenses.forms;
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
public class SpendingRecordForm {

    private Long expenseId;

    private String date;

    private BigDecimal amount;

    private Long currencyId;
}
