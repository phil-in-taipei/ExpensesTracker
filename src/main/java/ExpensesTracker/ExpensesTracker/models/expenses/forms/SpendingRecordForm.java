package ExpensesTracker.ExpensesTracker.models.expenses.forms;
import lombok.*;

import java.math.BigDecimal;

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
